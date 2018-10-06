//
//  CreateAccountViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class CreateAccountViewController: UIViewController {
    
    // Name: Full name of new user ; STRING
    // Email: User e-mail ; STRING
    // Password: User password that'll be hashed ; STRING
    // ConfirmPassword: Repeated user password for confirmation ; STRING
    
    @IBOutlet weak var Name: UITextField!
    @IBOutlet weak var Email: UITextField!
    @IBOutlet weak var Password: UITextField!
    @IBOutlet weak var ConfirmPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // When 'Create Account' button is pressed.
    
    @IBAction func CreateAccountButton(_ sender: Any){
        
        
        var QueryType = "CreateAccount";
        let UserName = Name.text;
        let UserEmail = Email.text;
        var UserPassword = Password.text;
        let UserConfirmPassword = ConfirmPassword.text;
        
        
        // Self explanatory, returns a salted and hashed password
        func saltAndHash(password: String, salt: String) -> String{
            let hashedPassword = password + salt;
            // Kevin Code Here
            return hashedPassword;
        }
        
        // Generates salt for password
        func saltGenerator(length: Int) -> String{
            let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789";
            let key = (0..<length).compactMap{_ in characters.randomElement()};
            let salt = String(key);
            return salt;
        }
        
        // Checks if user is already registered by email.
        
        func isRegistered(email: String) -> Bool{
            var registered = false;
            let QueryType = "CheckRegistry";
            
            let url = URL(string: "someURL/insert.php");
            var request = URLRequest(url: url!);
            request.httpMethod = "POST"
            let post = "QueryType=\(QueryType)&email=\(String(describing: UserEmail))";
            request.httpBody = post.data(using: String.Encoding.utf8);
            
            let task = URLSession.shared.dataTask(with: request){ (data: Data?, response: URLResponse?, error: Error?) in
                
                if error != nil {
                    print("error=\(String(describing: error))")
                    return
                }
                
                print("response = \(String(describing: response))")
                
                do{
                    let json = try JSONSerialization.jsonObject(with: data!, options: []) as? NSDictionary
                    
                    if let parseJSON = json {
                        let tmpParse = parseJSON["registered"] as! Bool;
                        if tmpParse == true {
                            registered = true;
                        }
                    }
                }
                catch{
                    print(error);
                }
            }
            task.resume();
            return registered;
        }
        
        // Input validation:
        // (A) Are any of the fields empt?
        // (B) Is Email already registered?
        // (C) Do Passwords match?
        
        if (UserName!.isEmpty || UserEmail!.isEmpty || UserPassword!.isEmpty || UserConfirmPassword!.isEmpty){
            print("All fields are required.")
        }
        
        if(isRegistered(email: UserEmail!)){
            print("Email is already registered.")
        }
        
        if(UserPassword != UserConfirmPassword){
            print("Passwords don't match.")
        }
        
        // Finally register the user
        
        // Salt and Hash password
        let keyLength = 5;
        let Salt = saltGenerator(length: keyLength);
        UserPassword = saltAndHash(password: UserPassword!,salt: Salt)
        
        let url = URL(string: "someURL/insert.php");
        var request = URLRequest(url: url!);
        request.httpMethod = "POST"
        let post = "QueryType=\(QueryType)&name=\(String(describing: UserName))&email=\(String(describing: UserEmail))&password=\(String(describing: UserPassword))&salt=\(Salt)";
        request.httpBody = post.data(using: String.Encoding.utf8);
        
        let task = URLSession.shared.dataTask(with: request){ (data: Data?, response: URLResponse?, error: Error?) in
            
            if error != nil {
                print("error=\(String(describing: error))")
                return
            }
            
            print("response = \(String(describing: response))")
            
            do{
                let json = try JSONSerialization.jsonObject(with: data!, options: []) as? NSDictionary
                
                if let parseJSON = json {
                    let tmpParse = parseJSON["success"] as! Bool;
                    if tmpParse == true {
                        print("Query Succesful");
                    }
                }
            }
            catch{
                print(error);
            }
        }
        task.resume();
    }
    
    // We think is only needed if we send information from view to view
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToLogin"){
            let _ = segue.destination as! ViewController
        }
        else if (segue.identifier == "CreateAccount"){
            let _ = segue.destination as! ViewController
        }
    }
    
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
}
