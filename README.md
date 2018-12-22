Simple ASYNC multithreaded web app
============

    This is small web-app represents basic REST operations.  
    It's allows to make POST and GET calls to represent async  
    execution with persisting in H2.  

Clone repository. Execute in bash:  

./gradlew bootRun

curl -X POST http://localhost:8080/execute -H 'content-type: application/json' -d '[1,1, 25, 76, 12, 16, 65, 54, 12, 33, 45, 65,76]'  

or 

curl -X GET http://localhost:8080/execute 

Pray! :)

Requirements
--------------------
- git
- gradle
- java 8

License
=======

GNU General Public License