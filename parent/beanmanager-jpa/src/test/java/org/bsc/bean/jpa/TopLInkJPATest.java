package org.bsc.bean.jpa;

import org.junit.Test;

/**
 * Hello world!
 *
 */
public class TopLInkJPATest
{
    @Test
    public void create(  )
    {
        System.out.println( "Hello World!" );


        MyUserJpaController myUserController = new MyUserJpaController();

        MyUser user = new MyUser();

        user.setName( "TEST" );

        myUserController.create(user);

        myUserController.close();
    }
}
