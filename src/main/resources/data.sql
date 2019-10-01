insert into DESIGNATION_INFORMATION(DESIGNATION, LEVEL) values ('Director', 1);
insert into DESIGNATION_INFORMATION(DESIGNATION, LEVEL) values ('Manager', 2);
insert into DESIGNATION_INFORMATION(DESIGNATION, LEVEL) values ('Lead', 3);
insert into DESIGNATION_INFORMATION(DESIGNATION, LEVEL) values ('Developer', 4);
insert into DESIGNATION_INFORMATION(DESIGNATION, LEVEL) values ('DevOps', 4);
insert into DESIGNATION_INFORMATION(DESIGNATION, LEVEL) values ('QA', 4);
insert into DESIGNATION_INFORMATION(DESIGNATION, LEVEL) values ('Intern', 5);

insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(1,'Thor',NULL);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(2,'Iron Man',1);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(3,'Hulk',1);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(2,'Captain America',1);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(4,'War machine',2);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(4,'Vision',2);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(4,'Falcon',4);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(3,'Ant Man',4);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(5,'Spider Man',2);
insert into EMPLOYEE_INFORMATION(DESIGNATION_ID,EMPLOYEE_NAME,MANAGER_ID) values(4,'Black Widow',3);