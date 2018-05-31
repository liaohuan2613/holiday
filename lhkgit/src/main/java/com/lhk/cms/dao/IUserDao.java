package com.lhk.cms.dao;

import java.util.List;

import com.lhk.cms.bean.User;

/**
 * 用于User的查询dao
 * 
 */
public interface IUserDao {

	// 保存用户对象
	public void saveUser(User user);

	// 查询所有用户对象
	public List<User> findAllUser();

	// 查询用户对象通过id
	public User findUserById(Long id);

	// 查询用户对象通过id
	public User findUserByUsername(String username);

	// 查询用户对象通过id
	public User findUserByUsernameAndPassword(String username, String password);

	// 删除用户
	public void deleteUser(String ids);

	// 更新用户
	public void updateUser(User user);

}
