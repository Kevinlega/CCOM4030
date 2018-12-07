// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : ChangePasswordViewController.swift
// Description : View controller that changes a user's password.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit

class ChangePasswordViewController: UIViewController {

    // MARK: - Variables
    // Receiving input from user
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var answer: UITextField!
    
    // Flag to prevent non registered users from making a password change request.
    var UserCanBeAdded = false
    
    // MARK: - Change Password Action
    // Verify if all fields are entered and can proceed
    @IBAction func ChangeThePassword(_ sender: Any) {
        
        let answered = answer.text
        let UserEmail = email.text
        
        if (UserEmail!.isEmpty || answered!.isEmpty){
           self.present(Alert(title: "Error", message: "All fields are requiered.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
        else if(!(isRegistered(email: UserEmail!))){
            self.present(Alert(title: "Something went wrong.", message: "Cannot change password.", Dismiss: "Dismiss"),animated: true, completion: nil)
        } else{
            UserCanBeAdded = true
        }
    }
    
    
    // MARK: - Default Functions
    // When view loads
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboardWhenTappedAround()
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Segue Function
    // Makes sure that user is registered and changes user-password.
    // Performs segue for Login view.
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToLogin"){
            let _ = segue.destination as! LoginViewController
        }
            // If user is registered and gives valid input, hash and salt new password
            // and insert it in database.
        else if (segue.identifier == "ChangePassword"){
            if UserCanBeAdded{
                let answered = answer.text
                let UserEmail = email.text
                let response = ChangePassword(email: UserEmail!, answer: answered!)
                if (response){
                     self.present(Alert(title: "Email was Sent.", message: "Please update password by tomorrow.", Dismiss: "Dismiss"),animated: true, completion: nil)
                     let _ = segue.destination as! LoginViewController
                } else{
                     self.present(Alert(title: "Something went wrong.", message: "Cannot change password.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
            }
        }
    }
}
