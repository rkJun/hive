package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.JodaDateUtil;

@Entity
public class Comment extends Model {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;
    public Long postId;
    public Long userId;
    @Constraints.Required
    public String contents;
    @Constraints.Required
    public Date date;
    public String filePath;

    public static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);

    public Comment() {
        date = JodaDateUtil.today();
    }

    public static List<Comment> findCommentsByPostId(Long postId) {
        return find.where().eq("postId", postId).findList();
    }

    public static Long write(Comment comment) {
        Post.countUpCommentCounter(comment.postId);
        comment.save();
        return comment.id;
    }

    public static void deleteByPostId(Long postId) {
        List<Comment> targets = Comment.find.where().eq("postId", "" + postId).findList();

        // 루프 돌면서 삭제
        Iterator<Comment> target = targets.iterator();
        while (target.hasNext()) {
            Comment comment = target.next();
            comment.delete();
        }
    }

    public String calcPassTime() {
        // TODO 경계값 검사하면 망할함수. 나중에 라이브러리 쓸예정
        Calendar today = Calendar.getInstance();

        long dTimeMili = today.getTime().getTime() - this.date.getTime();

        Calendar dTime = Calendar.getInstance();
        dTime.setTimeInMillis(dTimeMili);

        if (dTimeMili < 60 * 1000) {
            return "방금 전";
        } else if (dTimeMili < 60 * 1000 * 60) {
            return dTime.get(Calendar.MINUTE) + "분 전";
        } else if (dTimeMili < 60 * 1000 * 60 * 24) {
            return dTime.get(Calendar.HOUR) + "시간 전";
        } else if (dTimeMili < 60 * 1000 * 60 * 24 * 30) {
            return dTime.get(Calendar.DATE) + "일 전";
        } else if (dTimeMili < 60 * 1000 * 60 * 24 * 30 * 12) {
            return dTime.get(Calendar.MONDAY) + "달 전";
        } else {
            return dTime.get(Calendar.YEAR) + "년 전";
        }
    }

    public String findWriter() {
        return User.findNameById(userId);
    }
}