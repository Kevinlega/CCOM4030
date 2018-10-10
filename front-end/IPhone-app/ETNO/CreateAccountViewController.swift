//
//  CreateAccountViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los 5. All rights reserved.
//
import UIKit
import Foundation

class CreateAccountViewController: UIViewController {
    
    // Name: Full name of new user ; STRING
    // Email: User e-mail ; STRING
    // Password: User password that'll be hashed ; STRING
    // ConfirmPassword: Repeated user password for confirmation ; STRING
    
    // MARK: - Text Fields and Variables
    @IBOutlet weak var Name: UITextField!
    @IBOutlet weak var Email: UITextField!
    @IBOutlet weak var Password: UITextField!
    @IBOutlet weak var ConfirmPassword: UITextField!
   
    var UserCanBeAdded = false
    var QueryType = "CreateAccount"

    
    // MARK: - Create User Action (Button Press)
    
    // When 'Create Account' button is pressed.
    // Input validation:
    // (A) Are any of the fields empty?
    // (B) Is Email already registered?
    //      (1) Is it even an email?
    // (C) Do Passwords match?
    
    @IBAction func CanUserBeCreated(_ sender: Any) {
        let UserPassword = Password.text
        let UserName = Name.text
        let UserEmail = Email.text
        let UserConfirmPassword = ConfirmPassword.text
        
        if (UserName!.isEmpty || UserEmail!.isEmpty || UserPassword!.isEmpty || UserConfirmPassword!.isEmpty){
            
            let alertController = UIAlertController(title: "Error", message: "All fields are requiered.", preferredStyle: UIAlertController.Style.alert)
            alertController.addAction(UIAlertAction.init(title: "Dismiss", style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
            
            self.present(alertController, animated: true, completion: nil)
        }
            
        else{ if(isRegistered(email: UserEmail!)){
                
                let alertController = UIAlertController(title: "Error", message: "Email is already registered.", preferredStyle: UIAlertController.Style.alert)
                alertController.addAction(UIAlertAction.init(title: "Dismiss", style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
                
                self.present(alertController, animated: true, completion: nil)
                }
            
            else{ if(UserPassword! != UserConfirmPassword!){
                    
                    let alertController = UIAlertController(title: "Error", message: "Passwords don't match.", preferredStyle: UIAlertController.Style.alert)
                    alertController.addAction(UIAlertAction.init(title: "Dismiss", style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
                    
                    self.present(alertController, animated: true, completion: nil)
                 }
                  else{
                    UserCanBeAdded = true
                    }
                }
            }
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
    
    // MARK: - Password Handlers
    // Self explanatory, returns a salted and hashed password
    func saltAndHash(password: String, salt: String) -> String{
        let hashedPassword = password + salt;
        return String(hashedPassword.hashValue)
    }

    // Generates salt for password
    func saltGenerator(length: Int) -> String{
        let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789";
        let key = (0..<length).compactMap{_ in characters.randomElement()};
        let salt = String(key);
        return salt;
    }
    
    // MARK: - Verify if Registration is Posible
    // Checks if user is already registered by email.
    
    func isRegistered(email: String) -> Bool{
        let QueryType = "0";
        var done = false;
        var registered = false;
        let url = URL(string: "http://54.81.239.120/selectAPI.php");
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&email=\(email)";
        print(post)
        request.httpBody = post.data(using: String.Encoding.utf8);
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            
            if (error != nil) {
                print("error=\(error!)")
                return
            }
            do {
                let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                
                if let parseJSON = json {
                    let queryResponse = (parseJSON["registered"] as? Bool)!
                    print (queryResponse)
                    registered = queryResponse
                }
            }
            catch {
                print(error)
            }
            done = true;
        }
        task.resume()
        repeat {
            RunLoop.current.run(until: Date(timeIntervalSinceNow: 0.1))
        } while !done
        return registered;
    }

    // MARK: - Creates the Account
    // Create an account
    
    func CreateAccount(name: String, email: String, password: String, salt: String) -> Void{
        let QueryType = "0";
        var done = false;
        let url = URL(string: "http://54.81.239.120/insertAPI.php");
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&name=\(name)&email=\(email)&password=\(password)&salt=\(salt)";
        request.httpBody = post.data(using: String.Encoding.utf8);
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            
            if (error != nil) {
                print("error=\(error!)")
                return
            }
            // print("response = \(response!)")
            do {
                let json = try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                
                if let parseJSON = json {
                    let queryResponse = (parseJSON["registered"] as? Bool)!
                    if (queryResponse == true){
                        print("Account succesfully created.")
                    }
                    else{
                        print("Uh Oh")
                    }
                }
            }
            catch {
                print(error)
            }
            done = true;
        }
        task.resume()
        repeat {
            RunLoop.current.run(until: Date(timeIntervalSinceNow: 0.1))
        } while !done
        return
    }
    
    // MARK: - Segue Function
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToLogin"){
            let _ = segue.destination as! ViewController
        }
        else if (segue.identifier == "CreateAccount"){
            if UserCanBeAdded{
                
                // Finally register the user:
                // Salt and Hash password
                
                let _ = segue.destination as! ViewController
                
                var UserPassword = Password.text
                let UserName = Name.text
                let UserEmail = Email.text
                
               
                let keyLength = 5;
                let Salt = saltGenerator(length: keyLength)
                UserPassword = saltAndHash(password: UserPassword!,salt: Salt)
                CreateAccount(name: UserName!, email: UserEmail!,password: UserPassword!, salt: Salt)
                }
            }
        }
    }
