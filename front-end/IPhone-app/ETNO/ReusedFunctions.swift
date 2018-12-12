// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : ReusedFunctions.swift
// Description : A plethora of functions that will be used multiple times
//               accross the other views.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.


import Foundation
import UIKit

// MARK: - Password Handlers
// Returns a salted and hashed password, using md5 and string mapping
public func saltAndHash(password: String, salt: String) -> String{
    let hashedPassword = password + salt
    return (String(hashedPassword).md5!)
}

// Generates salt for password using string mapping of fixed character ammount
public func saltGenerator(length: Int) -> String{
    let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789"
    let key = (0..<length).compactMap{_ in characters.randomElement()}
    let  salt = String(key)
    return salt;
}

// MARK: - Alert Function
// Displays alert popup message
public func Alert(title: String, message: String, Dismiss: String) -> UIAlertController{
    let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
    alertController.addAction(UIAlertAction.init(title: Dismiss, style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("ALERT")}))
    
    return alertController
}

// MARK: - Connect to API
// Connects to API after receiving request and returns API response as json
public func ConnectToAPI(self: UIViewController,request: URLRequest) -> NSDictionary{
    
    var json : NSDictionary = NSDictionary()
    
    if ConnectionTest(self: self) {
        let group = DispatchGroup()
        group.enter()
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            do{
                // Server response
                json = try! JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! NSDictionary
                group.leave()
                }
            }
        // Wait until task is finished
        task.resume()
        group.wait()
    }
     return json
}

// MARK: - Verify if Registration is Posible
// Checks if user is already registered by email.

public func isRegistered(self: UIViewController,email: String) -> Bool{
    
    var response : NSDictionary = NSDictionary()
    // Create the request to the API
    // Tells API query to execute
    let QueryType = "0"
    // API URL
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    // Request method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&email=\(email)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    // Connect to API
    response = ConnectToAPI(self: self, request: request)
    // Return true if user is registered, false otherwise
    
    return (response["registered"] as? Bool ?? false)

}

// MARK: - Changes the Password
// Sends request for updating password
public func ChangePassword(self: UIViewController, email: String, answer: String) -> Bool{
    
    let _ = ConnectionTest(self: self)
    // Create the request to the API
    let QueryType = "4"
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&email=\(email)&answer=\(answer)"
    request.httpBody = post.data(using: String.Encoding.utf8)

    var json : NSDictionary = NSDictionary()
    let group = DispatchGroup()
    group.enter()
    
    let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
        do{
            // Server response
            json = try! JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! NSDictionary
            group.leave()
        }
    }
    // Wait until task is finished
    task.resume()
    group.wait()
    return json["inserted"] as? Bool ?? false
}

// MARK: - Creates the Account
// Create an account

public func CreateAccount(self: UIViewController, name: String, email: String, password: String, salt: String, answer: String) -> Bool{
    // Create the request to the API
    var response : NSDictionary = NSDictionary()
    // Tells API which query to execute
    let QueryType = "0"
    // API URL
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    // Request method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&name=\(name)&email=\(email)&password=\(password)&salt=\(salt)&answer=\(answer)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    
    // Receive response from API
    response = ConnectToAPI(self: self,request: request)
    
    if (response["registered"] as? Bool ?? false) == true{
        return true
    }
    else{
        return false
    }
}

// MARK: - Connection to Database
// Get projects from the user
public func GetProjects(self: UIViewController, user_id: Int) -> NSDictionary{
    // Create the request to the API
    // Tells API which query to execute
    let QueryType = "3"
    // API URL
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    // Request Method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&uid=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    // Conect to API
    return ConnectToAPI(self: self,request: request)
}

// MARK: - Creates the project
// Create a project
public func CreateProject(self: UIViewController, user_id: Int, name: String, description: String, location: String) -> NSDictionary{
    // Create the request to the API
    // Tells API which query to execute
    let QueryType = "2"
    // API URL
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    var request = URLRequest(url:url!)
    // Request Method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&name=\(name)&description=\(description)&location=\(location)&user_id=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    // Send request
    return ConnectToAPI(self: self,request: request)
}

// MARK: - CheckAdmin
// Verifies if a user is project admin
public func CheckAdmin(self: UIViewController, project_id: Int, user_id: Int) -> Bool{
    // Create the request to the API
    // Tells API which query to execute
    let QueryType = "8"
    // API URL
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    // Request Method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&pid=\(project_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    // Send request
    let response = ConnectToAPI(self: self,request: request)
    if (response["admin"] as? Int ?? -1) == user_id{
        return true
    }
    else{
        return false
    }
}

// MARK: - Login Handler
public func CheckLogin(self: UIViewController ,email: String, psw: String, Biometric: Bool) -> NSDictionary{
    
    // check if internet
    var internet = true
    guard let status = Network.reachability?.status else { return ["registered": false] }
        switch status {
        case .unreachable:
            internet = false
        case .wifi:
            break
        case .wwan:
            break
        }
    if internet {
        var password = psw
        var hashed_password = String()
        var salt = String()
        
        var response : NSDictionary
        
        // Create the request to the API
        // Tells API which query to execute
        var QueryType = "4"
        // API URL
        let url = URL(string: "http://54.81.239.120/selectAPI.php")
        var request = URLRequest(url:url!)
        // Request Method
        request.httpMethod = "POST"
        // Request parameters
        let post = "queryType=\(QueryType)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        
        // Connect to server
        response = ConnectToAPI(self: self,request: request)
        
        if (response["empty"] as? Bool ?? true) == false{
            hashed_password = response["hashed_password"] as! String
            salt = response["salt"] as! String
        }
        else{
            return ["registered": false]
        }
        
        // If login is not biometric ust salt and hash given password
        // if login is biometric password is given from icloud keychain
        if !Biometric{
            password = saltAndHash(password: password, salt: salt)
        }
        
        // Login is succesful, get user id
        if (password == hashed_password){
            
            // Create Request
            QueryType = "2";
            let url = URL(string: "http://54.81.239.120/selectAPI.php");
            var request = URLRequest(url:url!)
            request.httpMethod = "POST"
            let post = "queryType=\(QueryType)&email=\(email)";
            request.httpBody = post.data(using: String.Encoding.utf8);
            
            response = ConnectToAPI(self: self, request: request)
            if Biometric {
                return ["registered":true, "uid": response["uid"] as? Int ?? -1, "verified": response["verified"] as? Bool ?? false]
            } else {
                return ["registered":true, "uid": response["uid"] as? Int ?? -1, "verified": response["verified"] as? Bool ?? false, "hash":password]
            }
        }
        else {
            return ["registered": false]
        }
    } else {
         return ["registered": false]
    }
}

// MARK: - Create a Request
// Sends another user a friend request
public func SendRequest(self: UIViewController,user_id: Int, SelectedUsersEmail: [String] ) -> NSDictionary{
    
    // Tells API which query to execute
    let QueryType = "3"
    let url = URL(string: "http://54.81.239.120/insertAPI.php")
    // API URL
    var request = URLRequest(url:url!)
    var FailedEmail = [String()]
    
    // Request Method
    request.httpMethod = "POST"
    
    // Do for n selected emails
    for email in SelectedUsersEmail {
        // Request parameters
        let post = "queryType=\(QueryType)&uid=\(user_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        // Send request
        let response = ConnectToAPI(self: self, request: request)
        
        if (response["created"] as? Bool ?? false) == false{
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
// Get the pending friend requests of a user
func GetPendingRequest(self: UIViewController, user_id: Int) -> NSDictionary{
    
    // Tells API which query to execute
    let QueryType = "7";
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    
    // Request Method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&uid=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    // Send request
    return ConnectToAPI(self: self, request: request)
}


// MARK: - Retrieve All Friends
// Get the friends of user
public func GetFriends(self: UIViewController ,user_id: Int) -> NSDictionary {
    
    // Tells API which query to execute
    let QueryType = "6"
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    
    // Request Method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&uid=\(user_id)";
    request.httpBody = post.data(using: String.Encoding.utf8);
    // Send request
    return ConnectToAPI(self: self,request: request)
}


// MARK: - Get Participants of a project
// Get the users from the project
public func GetParticipants(self: UIViewController,project_id: Int, user_id: Int) -> NSDictionary{
    
    // Tells API which query to execute
    let QueryType = "1"
    // API URL
    let url = URL(string: "http://54.81.239.120/selectAPI.php")
    var request = URLRequest(url:url!)
    // Request Method
    request.httpMethod = "POST"
    // Request parameters
    let post = "queryType=\(QueryType)&pid=\(project_id)&uid=\(user_id)"
    request.httpBody = post.data(using: String.Encoding.utf8)
    // Send request
    return ConnectToAPI(self: self, request: request)
}

// MARK: - Save to keychain function
// Takes email and  hashed password and stores it in icloud keychain to be used for biometric login in the future.
public func SaveToKeychain(email: String, password: String){
    UserDefaults.standard.set(email, forKey: "lastAccessedUserName")
    let passwordItem = KeychainPasswordItem(service: KeychainConfiguration.serviceName, account: email, accessGroup: KeychainConfiguration.accessGroup)
    do {
        try passwordItem.savePassword(password)
    }
    catch{
        print("Error saving password")
    }
}

// MARK: - Handle Friend Requests
// Insert new users to the project
public func AnswerRequest(self: UIViewController ,user_id: Int, SelectedUsersEmail: [String] ) -> NSDictionary{
    
    // Tells API which query to execute
    let QueryType = "2"
    // API URL
    let url = URL(string: "http://54.81.239.120/updateAPI.php")
    var request = URLRequest(url:url!)
    var FailedEmail = [String()]
    // Request method
    request.httpMethod = "POST"
    
    // Insert n selected users to project
    for email in SelectedUsersEmail{
        // Request parameters
        let post = "queryType=\(QueryType)&uid=\(user_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        // Start task
        let response = ConnectToAPI(self: self,request: request)
        
        if response["updated"] as? Bool ?? false == false{
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

// Decline a user's friend request.
public func DeclineRequest(self: UIViewController, user_id: Int, SelectedUsersEmail: [String] ) -> NSDictionary{
    
    // Tells API query to be executed
    let QueryType = "3"
    // API URL
    let url = URL(string: "http://54.81.239.120/updateAPI.php")
    var request = URLRequest(url:url!)
    var FailedEmail = [String()]
    // Request method
    request.httpMethod = "POST"
    
    // Do for n selected emails
    for email in SelectedUsersEmail{
        // Request parameters
        let post = "queryType=\(QueryType)&uid=\(user_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        // Start task
        let response = ConnectToAPI(self: self, request: request)
        if (response["updated"] as? Bool ?? false) == false{
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
public func InsertParticipants(self: UIViewController,SelectedEmail: [String], project_id: Int){
    // Tells api which query will be executed
    let QueryType = "1";
    // API url
    let url = URL(string: "http://54.81.239.120/insertAPI.php");
    var request = URLRequest(url:url!)
    // Request method
    request.httpMethod = "POST"
    
    // Insert n emails to a project, where n is ammount of selected users
    for email in SelectedEmail{
        // Post parameters
        let post = "queryType=\(QueryType)&pid=\(project_id)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        // Start data task
        let response = ConnectToAPI(self: self, request: request)
        if (response["registered"] as? Bool ?? false) == false{
            print("COULD NOT INSERT")
        }
    }
}

//MARK: - String md5
// Return md5 hash for a string
public extension String{
    var md5: String?{
        guard let data = self.data(using: String.Encoding.utf8) else {return nil}
        let hash = data.withUnsafeBytes{(bytes: UnsafePointer<Data>) -> [UInt8] in
            var hash: [UInt8] = [UInt8](repeating: 0, count: Int(CC_MD5_DIGEST_LENGTH))
            CC_MD5(bytes, CC_LONG(data.count), &hash)
            return hash
        }
        
        return (hash.map { String(format: "%02x", $0) }.joined()) as String
    }
}

//MARK: - Keyboard hider
// hides keyboard
public extension UIViewController {
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}

//MARK: - Connection test
public func ConnectionTest(self: UIViewController) -> Bool{
   
    var internet = true
    
    // check if internet
    guard let status = Network.reachability?.status else { return false }
        switch status {
        case .unreachable:
            internet = false
        case .wifi:
            break
        case .wwan:
            break
    }
    
    
    var topController = UIApplication.shared.keyWindow?.rootViewController
    while let presentedViewController = topController!.presentedViewController {
        topController = presentedViewController
    }
    
    
    if (!internet && !(topController is LoginViewController )){
        AlertInternet(self: self)
        return false
    } else if(!internet){
        self.present(Alert(title: "No Internet Connection", message: "Try Again Later", Dismiss: "Dismiss"),animated: true, completion: nil)
        return false
    } else{
        return true
    }
}

// MARK: - Alert Internet
func AlertInternet(self: UIViewController){
    let refreshAlert = UIAlertController(title: "Lost Internet Connection.", message: "Do you want to log out or retry?.", preferredStyle: UIAlertController.Style.alert)
    
    refreshAlert.addAction(UIAlertAction(title: "LOG OUT", style: .default, handler: { (action: UIAlertAction!) in
        self.performSegue(withIdentifier: "Logout", sender: nil)
    }))
    
    refreshAlert.addAction(UIAlertAction(title: "RETRY", style: .cancel, handler: { (action: UIAlertAction!) in
        self.viewDidLoad()
    }))
    
    self.present(refreshAlert, animated: true, completion: nil)
}
