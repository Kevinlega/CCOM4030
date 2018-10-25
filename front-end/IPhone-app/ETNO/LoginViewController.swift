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
                let response = CheckLogin(email: emailField.text!, psw: passwordField.text!)
                if (response["registered"] as! Bool) == true{
                    let vc = segue.destination as! DashboardViewController
                    vc.user_id = response["uid"] as! Int
                }
                else{
                     self.present(Alert(title: "Error", message: "Credentials are incorrect.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
            }
        }
    }
}
