//
//  ChangePasswordViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/13/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
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
                let initialValue = GetInitialValue(email: UserEmail!)
                let salt = saltGenerator(length: 5,initialValue: initialValue)
                UserPassword = LFSR(data: UserPassword!, initialValue: initialValue)
                UserPassword = saltAndHash(password: UserPassword!, salt: salt)
                ChangePassword(email: UserEmail!, password: UserPassword!)
                let _ = segue.destination as! LoginViewController
            }
        }
    }
}
