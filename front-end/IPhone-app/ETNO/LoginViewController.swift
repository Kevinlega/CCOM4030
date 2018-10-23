//
//  LoginViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/10/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit


class LoginViewController: UIViewController {

    // MARK: - Variables
    //    This will be equal to database response
    var user_id = Int()
    
    @IBOutlet weak var emailField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    var CanSendLogin = false
    
    
    // MARK: - Verify if Login can Happen
    @IBAction func CanLogin(_ sender: Any) {
        
        let email = emailField.text
        let password = passwordField.text
        
        if (password!.isEmpty || email!.isEmpty  ){
            self.present(Alert(title: "Error", message: "All fields are requiered.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
        else{
            CanSendLogin = true
        }
    }
    
    // MARK: - Login Handler
    func CheckLogin() -> Bool{
        let email = emailField.text
        var password = passwordField.text
        var hashed_password = String()
        var salt = String()
        
//        var response : NSDictionary
        
        // Create the request to the API
        var QueryType = "4"
        let url = URL(string: "http://54.81.239.120/selectAPI.php")
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&email=\(email!)"
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
                let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                
                if let parseJSON = json {
                    hashed_password = parseJSON["hashed_password"] as! String
                    salt = parseJSON["salt"] as! String
                    
                }
            }
            catch {
                print("hi")
            }
            group.leave()
        }
        task.resume()
        group.wait()
        
        
//        if let parseJSON = json {
//            hashed_password = parseJSON["hashed_password"] as! String
//            salt = parseJSON["salt"] as! String
        
        password = saltAndHash(password: password!, salt: salt)
        
        if (password == hashed_password){
            QueryType = "2";
            let url = URL(string: "http://54.81.239.120/selectAPI.php");
            var request = URLRequest(url:url!)
            
            request.httpMethod = "POST"
            let post = "queryType=\(QueryType)&email=\(email!)";
            
            request.httpBody = post.data(using: String.Encoding.utf8);
            
            
//            copy from above
            let group = DispatchGroup()
            group.enter()
            
            let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
                do{
                    let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String]
                    self.user_id = Int(json[0])!
                    group.leave()
                
                }   catch{}
            }
            
            task.resume()
            group.wait()
            return true
        }
        else {
            return false
        }
    }
    
    
    // MARK: - Password Handlers
    // Self explanatory, returns a salted and hashed password
    func saltAndHash(password: String, salt: String) -> String{
        let hashedPassword = password + salt;
        return String(hashedPassword.hash)
    }
    
    
    // MARK: - Default Functions
    override func viewDidLoad() {
        // Do any additional setup after loading the view, typically from a nib.
        super.viewDidLoad()
    }
    
    override func didReceiveMemoryWarning() {
        // Dispose of any resources that can be recreated.
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Segue Function
    // We think is only needed if we send information from view to view
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "CreateAccount"){
            let _ = segue.destination as! CreateAccountViewController
        }
        else if (segue.identifier == "ChangePassword"){
            let _ = segue.destination as! ChangePasswordViewController
        }
        else if (segue.identifier == "Dashboard"){
            if CanSendLogin{
                if CheckLogin(){
                    let vc = segue.destination as! DashboardViewController
                    vc.user_id = user_id
                }
                else{
                     self.present(Alert(title: "Error", message: "Credentials are incorrect.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
            }
        }
    }
}
