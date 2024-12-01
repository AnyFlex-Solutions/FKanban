package com.fkanban.fkanban;

import com.fkanban.fkanban.appuser.AppUserServiceTest;
import com.fkanban.fkanban.appuser.PasswordResetServiceTest;
import com.fkanban.fkanban.appuser.UserControllerTest;
import com.fkanban.fkanban.email.EmailServiceTest;
import com.fkanban.fkanban.errors.MyErrorControllerTest;
import com.fkanban.fkanban.inout.inoutControllerTest;
import com.fkanban.fkanban.kanbans.KanbanControllerTest;
import com.fkanban.fkanban.kanbans.KanbanServiceTest;
import com.fkanban.fkanban.kanbans.MoSCoW.MoSCoWControllerTest;
import com.fkanban.fkanban.kanbans.invite.InvitationControllerTest;
import com.fkanban.fkanban.kanbans.invite.InvitationServiceTest;
import com.fkanban.fkanban.kanbans.kano.KanoControllerTest;
import com.fkanban.fkanban.pdf.PdfControllerTest;
import com.fkanban.fkanban.registration.RegistrationServiceTest;
import com.fkanban.fkanban.registration.UserRegistrationControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        FKanbanApplicationTests.class,
        RegistrationServiceTest.class,
        PdfControllerTest.class,
        UserRegistrationControllerTest.class,
        KanbanServiceTest.class,
        KanbanControllerTest.class,
        MoSCoWControllerTest.class,
        KanoControllerTest.class,
        InvitationServiceTest.class,
        InvitationControllerTest.class,
        inoutControllerTest.class,
        MyErrorControllerTest.class,
        EmailServiceTest.class,
        UserControllerTest.class,
        PasswordResetServiceTest.class,
        AppUserServiceTest.class
})
public class AllTests {
}