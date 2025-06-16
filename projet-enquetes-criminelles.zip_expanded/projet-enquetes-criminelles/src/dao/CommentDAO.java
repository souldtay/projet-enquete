// src/dao/CommentDAO.java
package dao;
import model.Comment;
public class CommentDAO extends AbstractDAO<Comment> {
    @Override protected String file() { return "data/comments.csv"; }
    @Override protected Class<Comment> type() { return Comment.class; }
}
