package com.example.employee_chart_temp.Controller;

import com.example.employee_chart_temp.EmployeeChartTempApplication;
import com.example.employee_chart_temp.EmployeeChartTempApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.testng.Assert.*;

@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = {EmployeeChartTempApplication.class})
public class ControllerClassTest extends AbstractTransactionalTestNGSpringContextTests
{
    @Autowired
    WebApplicationContext context;
    private MockMvc mvc;
    @BeforeMethod
    public void setUp()
    {
        mvc= MockMvcBuilders.webAppContextSetup(context).build();
    }

    //*******************************************************Test for get All*******************************************************************
    @Test(priority = 1)
    public void getAllTest() throws Exception
    {
        MvcResult result=mvc.perform(MockMvcRequestBuilders.get("/rest/employees"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        String jsonOutput=result.getResponse().getContentAsString();
        int length= JsonPath.parse(jsonOutput).read("$.length()");
        Assert.assertTrue(length>0);
    }
    //*******************************************************Test for get Specific*******************************************************************
    @Test(priority = 2)
    public void getUserTest() throws Exception
    {
        MvcResult result=mvc.perform(MockMvcRequestBuilders.get("/rest/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andDo(print()).andReturn();
        String jsonOutput=result.getResponse().getContentAsString();
        int length= JsonPath.parse(jsonOutput).read("$.length()");
        System.out.println(length);
        Assert.assertTrue(length>0);
    }
    @Test(priority = 3)
    public void getUserTestInvalidParent() throws Exception
    {
        MvcResult result=mvc.perform(MockMvcRequestBuilders.get("/rest/employees/11"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(print()).andReturn();
    }
    @Test(priority = 4)
    public void getUserTestNullParent() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/rest/employees/null"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andDo(print()).andReturn();
    }

    //************************************************test methods for put

    @Test
    public void putEmpWithNoData() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("","",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void putEmpWithInvalidPartialData() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void putEmpWithDemotion() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","intern",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void putEmpWithDemotionPossible() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","lead",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void putEmpWithPromotionPossible() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Manager",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void putEmpWithPromotion() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Director",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void replaceWithDirector() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Director",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }


    ///////////////////////////////////////  putApi false /////////////////////////////////////
    @Test
    public void updateEmpInvalidId() throws Exception
    {
        EmployeePut employee = new EmployeePut("Akashdeep","Manager",2,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/13").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }
    @Test
    public void updateEmpNoData() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,null,null,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void updateEmpInvalidParId() throws Exception
    {
        EmployeePut employee = new EmployeePut("Akash","Lead",12343,false);
        //NewEmployee employee = new NewEmployee(12343,"Mohit","lead",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpPromotion() throws Exception
    {
        EmployeePut employee = new EmployeePut("Akash","Director",1,false);
        //NewEmployee employee = new NewEmployee(1,"Mohit","Director",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpDemotion() throws Exception
    {
        EmployeePut employee = new EmployeePut("Akash","Lead",1,false);
        //NewEmployee employee = new NewEmployee(1,"Mohit","lead",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/21").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    public void updateEmpDemoteDirector() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,"lead",null,false);
        //NewEmployee employee = new NewEmployee(null,"","lead",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpDirectorName() throws Exception
    {
        EmployeePut employee = new EmployeePut("Akashdeep",null,null,false);
        //NewEmployee employee = new NewEmployee(null,"Rajat","",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void updateEmpDirectorWithDirector() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,"director",null,false);
        //NewEmployee employee = new NewEmployee(null,"","director",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void updateEmpDirectorWithOutDirector() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,"manager",null,false);
        //NewEmployee employee = new NewEmployee(null,"","manager",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpDirectorParChange() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,null,2,false);
        //NewEmployee employee = new NewEmployee(2,"","",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void hulkChildOfCaptain() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,null,4,false);
        //NewEmployee employee = new NewEmployee(4,"","",false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test(priority = 0)
    public void createEmployeeTest() throws Exception                //post is working or not
    {
        EmployeePost employeePost = new EmployeePost("wanda","intern",2);
        //CrudeEmployee employeePost=new CrudeEmployee(2,"wonder woman","intern");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        MvcResult result=mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//        String resultOutput=result.getResponse().getContentAsString();
//        Assert.assertEquals(employeePost,resultOutput);
    }

    @Test(priority = 1)
    public void directorValidationForManager() throws Exception                //Assigning director with manager
    {
        EmployeePost employeePost = new EmployeePost("wanda","director",2);
       // CrudeEmployee employeePost=new CrudeEmployee(2,"wonder woman","Director");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void multipleDirector() throws Exception                             //Adding second Director
    {
        EmployeePost employeePost = new EmployeePost("wanda","director",null);
        //CrudeEmployee employeePost=new CrudeEmployee(null,"wonder woman","Director");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void noData () throws Exception                //Adding employee with no data
    {
        EmployeePost employeePost = new EmployeePost();
        //CrudeEmployee employeePost=new CrudeEmployee();
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void partialData () throws Exception                //Adding employee with partial data
    {
        EmployeePost employeePost = new EmployeePost("wanda",null,2);
        //CrudeEmployee employeePost=new CrudeEmployee(2,"wonder woman");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void invalidParentId () throws Exception                //Adding employee with non existing manager
    {
        EmployeePost employeePost = new EmployeePost("wanda","Lead",12);
        //CrudeEmployee employeePost=new CrudeEmployee(12,"wonder woman", "Lead");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void hierarchyViolation () throws Exception                //Adding employee with violating organisation hierarchy
    {
        EmployeePost employeePost = new EmployeePost("wanda","Lead",8);
        //CrudeEmployee employee=new CrudeEmployee(8,"wonder woman", "Lead");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        employeePost.setJobTitle("manager");
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(mapper.writeValueAsString(employeePost)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void invalidDesignation () throws Exception                //Adding employee with non existing Designation
    {
        EmployeePost employeePost = new EmployeePost("wanda","Laed",12);
        //CrudeEmployee employeePost=new CrudeEmployee(12,"wonder woman", "Laead");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    /*********************** test case for delete employee ********************/

    @Test
    public void delInvalidId () throws Exception                //Deleting non existing employee
    {
        mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/121"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    public void delDirectorWithChild () throws Exception                //Deleting director with children
    {
        mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void delDirectorWithoutChild () throws Exception                //Deleting director with children
    {

        mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void delEmployee() throws Exception                //Deleting director without children
    {
        for(int i=10;i>0;i--)
        {
            mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/"+i))
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        }

    }

    @Test
    public void addDirectorToEmptyDatabase() throws Exception                //Re-adding director to the empty database after removing all the employees
    {
        for(int i=10;i>0;i--)
        {
            mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/"+i))
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        }

        EmployeePost employeePost = new EmployeePost("wanda","Director",null);
        //CrudeEmployee employeePost=new CrudeEmployee(null,"wonder woman","Director");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    }

    @Test
    public void addManagerToEmptyDatabase() throws Exception                //Re-adding manager to the empty database after removing all the employees
    {
        for(int i=10;i>0;i--)
        {
            mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/"+i))
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        }

        EmployeePost employeePost = new EmployeePost("wanda","manager",null);
        //CrudeEmployee employeePost=new CrudeEmployee(null,"wonder woman","manager");
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    }







//    @Test
//    public void testGet() throws Exception
//    {
//        mvc.perform(MockMvcRequestBuilders.get("/employee")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8)).andDo(print());
//    }
//    @Test
//    public void createEmployeeTest() throws Exception
//    {
//        EmployeePost employeePost=new EmployeePost("Manish","intern",2);
//        ObjectMapper mapper=new ObjectMapper();
//        String jsonInput=mapper.writeValueAsString(employeePost);
//        MvcResult result=mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//   }
}