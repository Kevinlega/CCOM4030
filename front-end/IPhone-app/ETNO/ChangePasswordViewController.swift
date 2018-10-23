//
//  ChangePasswordViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/13/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class ChangePasswordViewController: UIViewController {

    // MARK: - Variables
    
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var ConfirmPassword: UITextField!
    @IBOutlet weak var NewPassword: UITextField!
    
    var UserCanBeAdded = false
    
    // MARK: - Change Password Action
    // Verify if all fields are entered and can proceed
    @IBAction func ChangeThePassword(_ sender: Any) {
        
        let UserPassword = NewPassword.text
        let UserEmail = email.text
        let UserConfirmPassword = ConfirmPassword.text
        
        if (UserEmail!.isEmpty || UserPassword!.isEmpty || UserConfirmPassword!.isEmpty){
            
           self.present(Alert(title: "Error", message: "All fields are requiered.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
        else{ if(!(isRegistered(email: UserEmail!))){
            
            self.present(Alert(title: "Something went wrong.", message: "Cannot change password.", Dismiss: "Dismiss"),animated: true, completion: nil)
            
        }
        else{ if(UserPassword! != UserConfirmPassword!){
                
                self.present(Alert(title: "Error", message: "Passwords don't match.", Dismiss: "Dismiss"),animated: true, completion: nil)
            }
        else{
            UserCanBeAdded = true
            }
        }
    }
}
    
    // MARK: - Verifies if user exists
    
    func isRegistered(email: String) -> Bool{
        
        var registered = false
//        var response : NSDictionary
        
        // Create the request to the API
        let QueryType = "0"
        let url = URL(string: "http://54.81.239.120/selectAPI.php")
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&email=\(email)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        
//        response = ConnectToAPI(request: request)
        
        let group = DispatchGroup()
        group.enter()
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            
            if (error != nil) {
                print("error=\(error!)")
                return
            }
            do {
                let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                
                if let parseJSON = json {
                    let queryResponse = (parseJSON["registered"] as? Bool)!
                    registered = queryResponse
                }
            }
            catch {
                print(error)
            }
            group.leave()
        }
        task.resume()
        group.wait()
        
//        if let parseJSON = json {
//            let queryResponse = (parseJSON["registered"] as? Bool)!
//            registered = queryResponse
        
        return registered
    }
    
    // MARK: - Changes the Password
    func ChangePassword(email: String, password: String) {
        
        
        // Create the request to the API
        let QueryType = "0"
        let url = URL(string: "http://54.81.239.120/updateAPI.php")
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&email=\(email)&password=\(password)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in }
        task.resume()
    }
        
        
    // MARK: - Password Handlers
    // Self explanatory, returns a salted and hashed password
    func saltAndHash(password: String, salt: String) -> String{
        let hashedPassword = password + salt;
        return hashedPassword
        //        return String(hashedPassword.hashValue)
    }
    
    // Generates salt for password
    func saltGenerator(length: Int) -> String{
        let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789";
        let key = (0..<length).compactMap{_ in characters.randomElement()};
        let salt = String(key);
        return salt;
    }
    
    // MARK: - Default Functions
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Segue Function
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToLogin"){
            let _ = segue.destination as! LoginViewController
        }
        else if (segue.identifier == "ChangePassword"){
            if UserCanBeAdded{
                var UserPassword = NewPassword.text
                let UserEmail = email.text
                let salt = saltGenerator(length: 5)
                UserPassword = saltAndHash(password: UserPassword!, salt: salt)
                ChangePassword(email: UserEmail!, password: UserPassword!)
                let _ = segue.destination as! LoginViewController
            }
        }
    }
}
