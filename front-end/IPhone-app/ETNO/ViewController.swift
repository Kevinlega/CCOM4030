//
//  ViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 9/27/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
//    This will be equal to database response
    var user_id = Int()
    
    @IBOutlet weak var emailField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    var CanSendLogin = false
    
    @IBAction func CanLogin(_ sender: Any) {
        
        let email = emailField.text
        let password = passwordField.text
        
        if (password!.isEmpty || email!.isEmpty  ){
        
        let alertController = UIAlertController(title: "Error", message: "All fields are requiered.", preferredStyle: UIAlertController.Style.alert)
        alertController.addAction(UIAlertAction.init(title: "Dismiss", style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
        
        self.present(alertController, animated: true, completion: nil)
        }
        else{
        CanSendLogin = true
        }
}

func CheckLogin() -> Bool{
    var QueryType = "4"
    let email = emailField.text
    var password = passwordField.text
    var hashed_password = String()
    var salt = String()
    
    var done = false;
    let url = URL(string: "http://54.81.239.120/selectAPI.php");
    var request = URLRequest(url:url!)
    request.httpMethod = "POST"
    let post = "queryType=\(QueryType)&email=\(email!))";
    print(post)
    request.httpBody = post.data(using: String.Encoding.utf8);
    
    let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
        
        if (error != nil) {
            print("error=\(error!)")
            return
        }
        // print("response = \(response!)")
        do {
            let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
            
            if let parseJSON = json {
                hashed_password = parseJSON["hashed_password"] as! String
                salt = parseJSON["salt"] as! String
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
    
    
    password = saltAndHash(password: password!, salt: salt)
    
    if (password == hashed_password){
        QueryType = "2";
        let url = URL(string: "http://54.81.239.120/selectAPI.php");
        var request = URLRequest(url:url!)
        
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&email=\(email!)";
        
        request.httpBody = post.data(using: String.Encoding.utf8);
        
        let group = DispatchGroup()
        group.enter()
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            do
            {
                
                let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String]
                self.user_id = Int(json[0])!
                group.leave()
                
            }catch{
            }
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
        return String(hashedPassword.hashValue)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

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
                    let alertController = UIAlertController(title: "Error", message: "Credentials are incorrect.", preferredStyle: UIAlertController.Style.alert)
                    alertController.addAction(UIAlertAction.init(title: "Dismiss", style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
                    
                    self.present(alertController, animated: true, completion: nil)
                }
               
            }
            

            
        }
    }
}

