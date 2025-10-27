package com.denisol.tests;

import com.denisol.clients.UsersApiClient;

public abstract class BaseTest {

    protected final UsersApiClient usersApiClient = new UsersApiClient();
}