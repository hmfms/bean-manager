package org.bsc.bean.jpa;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

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


        MyDataJpaController myDataController = new MyDataJpaController();

        MyUserJpaController myUserController = new MyUserJpaController();

        MyData data = new MyData();
        data.setBalance(1000L);

        myDataController.create(data);

        MyUser user = new MyUser();

        user.setName( "TEST" );
        user.setData(data);


        myUserController.create(user);

        MyUser u = myUserController.findMyUser(user.getId());
        
        assertNotNull( u );
        
        assertNotNull( u.getData() );

        

        myDataController.close();
        myUserController.close();
    }
}
