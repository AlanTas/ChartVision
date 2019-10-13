package com.taslabs.chartvision.interfaces;

import java.util.List;

public interface IUserManager {

   void SaveUser(IUser user);
   void RemoveUser(IUser user);
   void EditUser(IUser user);
   List<IUser> getUsers();
}
