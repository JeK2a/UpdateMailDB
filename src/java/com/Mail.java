package com;

public class Mail {
    public static void main(String[] args) {

        User user_1 = new User(1);
        User user_2 = user_1;

        user_1.a = 2;

        System.out.println(user_2.a);

    }

}


class User{

    int a;

    public User(int a) {
        this.a = a;
    }
}
