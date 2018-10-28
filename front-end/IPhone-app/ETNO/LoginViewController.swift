//
//  LoginViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/10/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit
import LocalAuthentication

class LoginViewController: UIViewController {

    // MARK: - Variables
    //    This will be equal to database response
    var user_id = Int()
    var emailKeyChain = String()
    var passwordKeyChain = String()
    
    @IBOutlet weak var emailField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    var CanSendLogin = false
    var BiometricAuthentication = false
    
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
        BiometricLogin()
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
                var response : NSDictionary = NSDictionary()

                if BiometricAuthentication{
                    response = CheckLogin(email: emailKeyChain, psw: passwordKeyChain,Biometric: BiometricAuthentication)
                }
                else{
                    response = CheckLogin(email: emailField.text!, psw: passwordField.text!,Biometric: BiometricAuthentication)
                }
                if (response["registered"] as! Bool) == true{
                    if response["verified"] as! Bool == true{
                        let vc = segue.destination as! DashboardViewController
                        vc.user_id = response["uid"] as! Int
                    }
                    else{
                        performSegue(withIdentifier: "NotVerified", sender: nil)
                    }
                    
                    
                }
                else{
                    self.present(Alert(title: "Error", message: "Credentials are incorrect.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
                
            }
        }
    }
    
    // MARK: - TOUCH/FACE ID
    // Salvame papi dios
    
    // Get password from keychain
    func LoadPassword(_ email: String){
        let passwordItem = KeychainPasswordItem(service: KeychainConfiguration.serviceName, account: email, accessGroup: KeychainConfiguration.accessGroup)
        do{
            self.passwordKeyChain = try passwordItem.readPassword()
            // Authenticate user using stored password from keychain. (Remember email is already registered and password is already hashed.)
            self.BiometricAuthentication = true
            self.CanSendLogin = true
            
        } catch KeychainPasswordItem.KeychainError.noPassword {
            print("No saved password")
        } catch {
            print("Unhandled error")
        }
    }
    
    // MARK: - Biometric login
    // If user presses biometric login button:
    // Present biometric login window
    // Take user to his dashboard if he is authenticated.
    
    func BiometricLogin(){
        //  Request biometric authentication, we look for last accessed email in app and display authentication window.
        let context = LAContext()
        
        if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil){
            guard let email = UserDefaults.standard.object(forKey: "lastAccessedUserName") as? String else {return}
            context.evaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics, localizedReason: email, reply: { (authSuccessful, authError) in
                if authSuccessful{
                    self.emailKeyChain = email
                    self.LoadPassword(email)
                    
                    DispatchQueue.main.async{
                        self.performSegue(withIdentifier: "Dashboard", sender: nil)
                    }
                }
            })
        }
    }
}
