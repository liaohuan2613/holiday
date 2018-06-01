package com.lhk.cms.dao;

import java.util.List;

import com.lhk.cms.bean.Comment;

public interface ICommentDao {
	//保存评论对象
	public void saveComment(Comment comment);

	//查询所有评论对象
	public List<Comment> findAllComment();
	
	//查询所有评论对象
	public List<Comment> findAllCommentByMicroBlogId(Long microBlogId);

	//查询所有评论对象
	public List<Comment> findAllCommentByUserId(Long userId);

	//查询评论对象通过id
	public Comment findCommentById(Long id);

	//删除评论
	public void deleteComment(String ids);

	//更新评论
	public void updateComment(Comment comment);
}
