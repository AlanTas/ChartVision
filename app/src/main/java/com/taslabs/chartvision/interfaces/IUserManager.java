package com.taslabs.chartvision.interfaces;

import java.util.List;

public interface IUserManager {

   void SaveUser(IUser user);
   List<IUser> getUsers();
}
