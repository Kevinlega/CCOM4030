//
//  ReusedFunctions.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/14/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import Foundation
import UIKit

// MARK: - Password Handlers
// Self explanatory, returns a salted and hashed password
public func saltAndHash(password: String, salt: String) -> String{
    let hashedPassword = password + salt
    return String(hashedPassword.hash)
}

// Generates salt for password
public func saltGenerator(length: Int,initialValue: UInt) -> String{
    let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789"
    let key = (0..<length).compactMap{_ in characters.randomElement()}
    var  salt = String(key)
    salt = LFSR(data: salt, initialValue: initialValue)
    return salt;
}

// MARK: - Alert Function

public func Alert(title: String, message: String, Dismiss: String) -> UIAlertController{
    let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
    alertController.addAction(UIAlertAction.init(title: Dismiss, style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
    
    return alertController
}

// MARK: - Get the Last Bit
public func theLowestBit(newState: UInt) -> Character{
    let lastBit = String(newState,radix: 2)
    return lastBit.last!
}

// MARK: - Change the InitialValue
public func feedback(initialValue: UInt) -> UInt{
    var newState = initialValue
    
    for _ in 1...10{
        if theLowestBit(newState: newState) == "0"{
            newState = newState>>1
        }
        else{
            newState = (newState>>1) ^ 0x85913829
        }
    }
    return newState
}

// MARK: - LFSR Algorithm
public func LFSR(data: String, initialValue: UInt)-> String {

    var newState = initialValue
    var hexadecimal : String
    var output = ""
    
    for letter in data {
        newState = feedback(initialValue: newState)
        let char = String(letter).unicodeScalars
        let charInt = UInt(char[char.startIndex].value)
        hexadecimal = String(newState ^ (charInt & UInt(0xFF)))
        output.append(hexadecimal)
    }
    return output
}

// MARK: - Generate Random InitialValue for LFSR
public func generateRandomUInt() -> UInt{
    return UInt.random(in: 0..<4294967295)
}

// MARK: - Connect to API
public func ConnectToAPI(request: URLRequest) -> NSDictionary{
    
    var json : NSDictionary = NSDictionary()
    let group = DispatchGroup()
    group.enter()
    
    let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
        do{
            json = try! JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! NSDictionary
            group.leave()
        }
        }
    task.resume()
    group.wait()
    return json
}

// MARK: - Verify if Registration is Posible
// Checks if user is already registered by email.

public func isRegistered(email: String) -> Bool{
    
    var response : NSDictionary = NSDictionary()
    
    // Create the request to the API
    let QueryType = "0"
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&email=\(email)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
    
    response = ConnectToAPI(request: request)
    
    return (response["registered"] as! Bool)
}

// MARK: - Changes the Password
public func ChangePassword(email: String, password: String, initialValue: UInt,salt: String) {
    
    // Create the request to the API
    let QueryType = "0"
    let url = URL(string: "http://54.81.239.120/updateAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&email=\(email)&password=\(password)&initialValue=\(initialValue)&salt=\(salt)"
    request.httpBody = post.data(using: String.Encoding.utf8)

    let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in }
    task.resume()
}

// MARK: - Creates the Account
// Create an account

public func CreateAccount(name: String, email: String, password: String, salt: String, initialValue: UInt) -> Bool{
    
    // Create the request to the API
    var response : NSDictionary = NSDictionary()
    
    let QueryType = "0"
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"

    let post = "queryType=\(QueryType)&name=\(name)&email=\(email)&password=\(password)&salt=\(salt)&initialValue=\(initialValue))"

    request.httpBody = post.data(using: String.Encoding.utf8)
    
    response = ConnectToAPI(request: request)
    
    if (response["registered"] as? Bool) == true{
        return true
    }
    else{
        return false
    }
}

// MARK: - Connection to Database
public func GetProjects(user_id: Int) -> NSDictionary{
    // Create the request to the API
    let QueryType = "3"
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&uid=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
    return ConnectToAPI(request: request)
}

// MARK: - Creates the project

public func CreateProject(user_id: Int, name: String, description: String, location: String) -> NSDictionary{
    let folder_link = "the_link"
    
    // Create the request to the API
    let QueryType = "2"
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&name=\(name)&description=\(description)&location=\(location)&folder_link=\(folder_link)&user_id=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
   return ConnectToAPI(request: request)
}

// MARK: - verifies if is admin
public func CheckAdmin(project_id: Int, user_id: Int) -> Bool{
    // Create the request to the API
    let QueryType = "8"
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&pid=\(project_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
    let response = ConnectToAPI(request: request)
    if (response["admin"] as! Int) == user_id{
        return true
    }
    else{
        return false
    }
}


// MARK: - Login Handler
public func CheckLogin(email: String, psw: String) -> NSDictionary {
    var password = psw
    var hashed_password = String()
    var salt = String()
    var initialValue : UInt
    
    var response : NSDictionary
    
    // Create the request to the API
    var QueryType = "4"
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&email=\(email)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
    response = ConnectToAPI(request: request)
    
    if (response["empty"] as! Bool) == false{
        hashed_password = response["hashed_password"] as! String
        salt = response["salt"] as! String
        initialValue = response["initialValue"] as! UInt
    }
    else{
        return ["registered": false]
    }
    
    password = LFSR(data: password, initialValue: initialValue)
    password = saltAndHash(password: password, salt: salt)
    
    if (password == hashed_password){
        // Create Request
        QueryType = "2";
        let url = URL(string: "http://54.81.239.120/selectAPI.php");
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&email=\(email)";
        request.httpBody = post.data(using: String.Encoding.utf8);
        
        response = ConnectToAPI(request: request)
        
        return ["registered":true, "uid": response["uid"] as! Int]
    }
    else {
        return ["registered": false]
    }
}

// MARK: - Create a Request
// Insert new users to the project

public func SendRequest(user_id: Int, SelectedUsersEmail: [String] ) -> NSDictionary {
    
    let QueryType = "3"
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    var FailedEmail = [String()]
    
    request.httpMethod = "POST"
    
    for email in SelectedUsersEmail {
        
        let post = "queryType=\(QueryType)&uid=\(user_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        let response = ConnectToAPI(request: request)
        
        if response["inserted"] as! Bool == false{
            FailedEmail.append(email)
        }
    }
    if FailedEmail.count == 0{
        return ["success": true]
    }
    else{
        return ["success": false, "Failed": FailedEmail]
    }
}


// MARK: - Retrieve Pending Requests
// Get the Users from the database

func GetPendingRequest(user_id: Int) -> NSDictionary{
    
    let QueryType = "7";
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&uid=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
    return ConnectToAPI(request: request)
}


// MARK: - Retrieve All Friends
// Get the Users from the database

public func GetFriends(user_id: Int) -> NSDictionary {
    
    let QueryType = "6"
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&uid=\(user_id)";
    request.httpBody = post.data(using: String.Encoding.utf8);
    
    return ConnectToAPI(request: request)
}


// MARK: - Get Participants of a project
// Get the Users from the database

public func GetParticipants(project_id: Int, user_id: Int) -> NSDictionary{
    
    let QueryType = "1";
    let url = URL(string: "http://54.81.239.120/selectAPI.php");
    var request = URLRequest(url:url!)
    
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&pid=\(project_id)&uid=\(user_id)";
    request.httpBody = post.data(using: String.Encoding.utf8);
    
    return ConnectToAPI(request: request)
}
