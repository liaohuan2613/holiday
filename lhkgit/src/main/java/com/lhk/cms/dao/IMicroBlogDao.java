package com.lhk.cms.dao;

import java.util.List;

import com.lhk.cms.bean.MicroBlog;

public interface IMicroBlogDao {
	//保存微博对象
	public void saveMicroBlog(MicroBlog microBlog);

	//查询所有微博对象
	public List<MicroBlog> findAllMicroBlog();
	
	//查询所有微博对象
	public List<MicroBlog> findAllMicroBlogByUserId(Long userId);

	//查询微博对象通过id
	public MicroBlog findMicroBlogById(Long id);

	//删除微博
	public void deleteMicroBlog(String ids);

	//更新微博
	public void updateMicroBlog(MicroBlog microBlog);
}
