package com.taslabs.chartvision.interfaces;

import java.util.List;

public interface IUserManager {

   boolean SaveUser(IUser user);
   void RemoveUser(IUser user);
   boolean EditUser(IUser oldUser, IUser newUser);
   List<IUser> getUsers();
}
