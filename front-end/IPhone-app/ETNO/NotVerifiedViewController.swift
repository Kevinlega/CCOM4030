//
//  NotVerifiedViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/27/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class NotVerifiedViewController: UIViewController {
    
    
    // MARK: - Variables
    // Receiving input from user
    @IBOutlet weak var email: UITextField!
    
    // Flag to prevent non registered users from making a password change request.
    var VerifyAble = false
    
    // MARK: - Change Password Action
    // Verify if all fields are entered and can proceed
    func verify() {
        
        let UserEmail = email.text
        
        if (UserEmail!.isEmpty){
            self.present(Alert(title: "Error", message: "Field is requiered.", Dismiss: "Dismiss"),animated: true, completion: nil)
        } else{
            VerifyAble = true
        }
    }

    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
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
        else if (segue.identifier == "Verify"){
            let UserEmail = email.text!
            verify()
            if VerifyAble {
                print("hr")
                if(!(isRegistered(email: UserEmail))){
                    self.present(Alert(title: "Something went wrong.", message: "Cannot change password.", Dismiss: "Dismiss"),animated: true, completion: nil)
                } else{
                    
                    print("here")
                    
                    var response : NSDictionary = NSDictionary()
                
                    // Create the request to the API
                    let QueryType = "1"
                    let url = URL(string: "http://54.81.239.120/updateAPI.php")
                    var request = URLRequest(url:url!)
                    request.httpMethod = "POST"
                    let post = "queryType=\(QueryType)&email=\(UserEmail)"
                    request.httpBody = post.data(using: String.Encoding.utf8)
                
                    response = ConnectToAPI(request: request)
                    
                    print(response)
                    if (response["updated"] as! Bool == true){
                        let _ = segue.destination as! LoginViewController
                    } else{
                        self.present(Alert(title: "Error", message: "Something went wrong, try again.", Dismiss: "Dismiss"),animated: true, completion: nil)
                    }
                }
            }
        }
    }
}
