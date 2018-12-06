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
    return (String(hashedPassword).md5!)
}

// Generates salt for password
public func saltGenerator(length: Int) -> String{
    let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789"
    let key = (0..<length).compactMap{_ in characters.randomElement()}
    let  salt = String(key)
    return salt;
}

// MARK: - Alert Function

public func Alert(title: String, message: String, Dismiss: String) -> UIAlertController{
    let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
    alertController.addAction(UIAlertAction.init(title: Dismiss, style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
    
    return alertController
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
public func ChangePassword(email: String, password: String, salt: String) {
    
    // Create the request to the API
    let QueryType = "0"
    let url = URL(string: "http://54.81.239.120/updateAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&email=\(email)&password=\(password)&salt=\(salt)"
    request.httpBody = post.data(using: String.Encoding.utf8)

    let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in }
    task.resume()
}

// MARK: - Creates the Account
// Create an account

public func CreateAccount(name: String, email: String, password: String, salt: String) -> Bool{
    
    // Create the request to the API
    var response : NSDictionary = NSDictionary()
    
    let QueryType = "0"
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"

    let post = "queryType=\(QueryType)&name=\(name)&email=\(email)&password=\(password)&salt=\(salt)"

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
    
    // Create the request to the API
    let QueryType = "2"
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&name=\(name)&description=\(description)&location=\(location)&user_id=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    print(post)
    
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
public func CheckLogin(email: String, psw: String, Biometric: Bool) -> NSDictionary {
    var password = psw
    var hashed_password = String()
    var salt = String()
    
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
    }
    else{
        return ["registered": false]
    }
    
    
    if !Biometric{
        password = saltAndHash(password: password, salt: salt)
    }
    
    if (password == hashed_password){
        // Create Request
        QueryType = "2";
        let url = URL(string: "http://54.81.239.120/selectAPI.php");
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&email=\(email)";
        request.httpBody = post.data(using: String.Encoding.utf8);
        
        response = ConnectToAPI(request: request)
        return ["registered":true, "uid": response["uid"] as! Int, "verified": response["verified"] as! Bool]
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
        
        if response["created"] as! Bool == false{
            FailedEmail.append(email)
        }
    }
    if FailedEmail.count == 1{
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
    
    let QueryType = "1"
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&pid=\(project_id)&uid=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
    return ConnectToAPI(request: request)
}

// MARK: - Save to keychain function
// Takes email and  hashed password and stores it in icloud keychain to be used for biometric login in the future.
public func SaveToKeychain(email: String, password: String) {
    UserDefaults.standard.set(email, forKey: "lastAccessedUserName")
    let passwordItem = KeychainPasswordItem(service: KeychainConfiguration.serviceName, account: email, accessGroup: KeychainConfiguration.accessGroup)
    do {
        try passwordItem.savePassword(password)
    } catch {
        print("Error saving password")
    }
}

// MARK: - Handle Friend Requests
// Insert new users to the project

public func AnswerRequest(user_id: Int, SelectedUsersEmail: [String] ) -> NSDictionary {
    
    let QueryType = "2"
    let url = URL(string: "http://54.81.239.120/updateAPI.php")
    var request = URLRequest(url:url!)
    var FailedEmail = [String()]
    
    request.httpMethod = "POST"
    
    for email in SelectedUsersEmail {
        
        let post = "queryType=\(QueryType)&uid=\(user_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        let response = ConnectToAPI(request: request)
        
        if response["updated"] as! Bool == false{
            FailedEmail.append(email)
        }
    }
    if FailedEmail.count == 1{
        return ["success": true]
    }
    else{
        return ["success": false, "Failed": FailedEmail]
    }
}

public func DeclineRequest(user_id: Int, SelectedUsersEmail: [String] ) -> NSDictionary {
    
    let QueryType = "3"
    let url = URL(string: "http://54.81.239.120/updateAPI.php")
    var request = URLRequest(url:url!)
    var FailedEmail = [String()]
    
    request.httpMethod = "POST"
    
    for email in SelectedUsersEmail {
        
        let post = "queryType=\(QueryType)&uid=\(user_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        let response = ConnectToAPI(request: request)
        
        if response["updated"] as! Bool == false{
            FailedEmail.append(email)
        }
    }
    if FailedEmail.count == 1{
        return ["success": true]
    }
    else{
        return ["success": false, "Failed": FailedEmail]
    }
}

// MARK: - Insert Users
// Insert new users to the project

public func InsertParticipants(SelectedEmail: [String], project_id: Int){
    
    let QueryType = "1";
    let url = URL(string: "http://54.81.239.120/insertAPI.php");
    var request = URLRequest(url:url!)
    
    request.httpMethod = "POST"
    
    for email in SelectedEmail {
        
        let post = "queryType=\(QueryType)&pid=\(project_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        
        let response = ConnectToAPI(request: request)
        
        if (response["registered"] as! Bool) == false{
            print("bad")
        }
    }
}

//MARK: - String md5
public extension String {
    
    var md5: String? {
        guard let data = self.data(using: String.Encoding.utf8) else { return nil }
        
        let hash = data.withUnsafeBytes { (bytes: UnsafePointer<Data>) -> [UInt8] in
            var hash: [UInt8] = [UInt8](repeating: 0, count: Int(CC_MD5_DIGEST_LENGTH))
            CC_MD5(bytes, CC_LONG(data.count), &hash)
            return hash
        }
        
        return (hash.map { String(format: "%02x", $0) }.joined()) as String
    }
}
