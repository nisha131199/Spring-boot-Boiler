package com.taskly.service.email

import com.taskly.respository.user.Users
import com.taskly.utils.autowired
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService {

    private val javaMailSender: JavaMailSender by autowired()

    @Value("\${application.security.url}")
    private val url: String = ""

    @Value("\${application.security.company_name}")
    private val companyName: String = ""

    @Async
    fun supportEmail(emailList: Array<String>, description: String, user: Users) {
        try {
            val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage)
            helper.setSubject("Support Request by ${user.firstname} ${user.lastname}.")
            val html = ("""
               <!DOCTYPE html>
               <html lang="en">
               <head>
                   <meta charset="UTF-8">
                   <meta name="viewport" content="width=device-width, initial-scale=1.0">
                   <title>Support Request</title>
                   <style>
                       body {
                           font-family: 'Verdana', sans-serif;
                           background-color: #f4f4f4;
                           text-align: center;
                           margin: 0;
                           padding: 0;
                       }

                       .container {
                           max-width: 600px;
                           margin: 50px auto;
                           background-color: #fff;
                           padding: 20px;
                           border-radius: 8px;
                           box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                       }

                       h1 {
                           color: #3498db;
                           
                       }
                       .left{
                        text-align:left;
                       }
                       p {
                           color: #555;
                       }

                       .button {
                           display: inline-block;
                           padding: 10px 20px;
                           margin-top: 5px;
                           background-color: #3498db;
                           color: #FFFFFF;
                           text-decoration: none;
                           border-radius: 5px;
                           transition: background-color 0.3s;
                       }
                       a{
                          color :#FFFFFF;
                       }

                       .button:hover {
                           background-color: #2980b9;
                       }
                   </style>
               </head>
               <body>
                   <div class="container">
                   <img src="https://10561b5a.rocketcdn.me/wp-content/uploads/2024/02/taskly-web-logo-larger.png" align="center" 
                        style="height:50px; width: 130px">
                       <br/>
                       <br/>
                       <h1>Support Request</h1>
                       <p class="left">Hi <b>Admin,</b></p>
                       <p class="left">You have received a new support request from ${user.firstname}. Below are the details of the request:</p>
                       <p class="left"><b>Subject: </b>TITLE</p>
                       <p class="left"><b>Message: </b>$description</p><br/>
                       <p class="left"><b>User name: </b>${user.firstname} ${user.lastname}</p>
                       <p class="left"><b>Mobile number: </b>${user.phone}</p>
                       <p class="left"><b>Email: </b>${user.email}</p>
                       <p class="left">Please review the support request and take the necessary action.</p>
                    <p class="left">Thanks,<br/>Team $companyName</p>
                   </div>
               </body>
               </html>
        """.trimIndent())
            helper.setText(html, true)
            helper.setTo(emailList)
            javaMailSender.send(mimeMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}