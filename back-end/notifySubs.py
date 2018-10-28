#!/usr/bin/python
# -*- coding:utf-8 -*-

import sys
import urllib, urllib2
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.application import MIMEApplication

def sendEmail(msg, email):
        try:
            	print"Sending Email..."
                server = smtplib.SMTP('smtp.gmail.com', 587)
                server = smtplib.SMTP_SSL()
                server.connect('smtp.gmail.com')
                server.login('info.etnografia.digital@gmail.com', 'etnografia123')
                print "Logged into email acount."
                server.sendmail('info.etnografia.digital@gmail.com', email, msg.as_string())
                server.quit()
                print "Email sent successfully."
        except smtplib.SMTPRecipientsRefused:
                print "Invalid email address: "+email
        except smtplib.SMTPAuthenticationError:
                print "Authorization error."
        except smtplib.SMTPSenderRefused:
                print "Invalid sender."
        except smtplib.SMTPException, e:
                print "Email error: "+str(e)

#ARGUMENTS LIST
arg = sys.argv #contains all the arguments from call
email = str(arg[1])
subject = str(arg[2])
msg = str(arg[3])

print("email: "+email)
print("subject: "+subject)
print("msg: "+msg)

#build for multipart message
msg = MIMEMultipart('mixed')
msg['Subject'] = subject
msg['From'] = 'info.etnografia.digital@gmail.com'
msg['To'] = email

alternative = MIMEMultipart('alternative')
textplain = MIMEText('', 'plain')
alternative.attach(textplain)

#evaluate answer, email is sent only if one of these two filter statements are met
htmlLoc =('/var/www/email/notifySubs.html')
htmlContent = open(htmlLoc).read()

#attach html to email
html = MIMEText(htmlContent, 'html')
alternative.attach(html)

#attach html to msg
msg.attach(alternative)

#call function to send email
sendEmail(msg, email)

