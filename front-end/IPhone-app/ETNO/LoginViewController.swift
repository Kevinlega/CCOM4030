// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        :  LoginViewController.swift
// Description : View controller that lets the user authenticate with the server
//              to pass to the dashboard and see all the projects
//               the user owns and participates, gateway to change password
//               and create account.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

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
        
        
        var internet = true
        
        // check if internet
        guard let status = Network.reachability?.status else { return }
        switch status {
        case .unreachable:
            internet = false
        case .wifi:
            break
        case .wwan:
            break
        }
        
        if(internet){
            BiometricLogin()
        }
        
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
        
        }
    
    override func didReceiveMemoryWarning() {
        // Dispose of any resources that can be recreated.
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Ask to store credentials
    func AlertCredentials(UserEmail: String, UserPassword: String){
        let refreshAlert = UIAlertController(title: "Biometric Access", message: "Do you want to store the credentials.", preferredStyle: UIAlertController.Style.alert)
        
        refreshAlert.addAction(UIAlertAction(title: "Yes", style: .default, handler: { (action: UIAlertAction!) in
            SaveToKeychain(email: UserEmail, password: UserPassword)
            self.performSegue(withIdentifier: "Move", sender: nil)
        }))
        
        refreshAlert.addAction(UIAlertAction(title: "No", style: .cancel, handler: { (action: UIAlertAction!) in
            print("Don't save")
            self.performSegue(withIdentifier: "Move", sender: nil)
        }))
        
        self.present(refreshAlert, animated: true, completion: nil)
    }
    
    
    
        
    // MARK: - Segue Function
    // Handles the data and if the login is successful passes data to next view
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier != "Logout"{
            let _ = ConnectionTest(self: self)
        }
        
        if (segue.identifier == "CreateAccount"){
            let _ = segue.destination as! CreateAccountViewController
        }
        else if (segue.identifier == "ChangePassword"){
            let _ = segue.destination as! ChangePasswordViewController
        } else if (segue.identifier == "Dashboard"){
    
            if CanSendLogin{
                var response : NSDictionary = NSDictionary()

                if BiometricAuthentication{
                    response = CheckLogin(self: self, email: emailKeyChain, psw: passwordKeyChain,Biometric: BiometricAuthentication)
                }
                else{
                    response = CheckLogin(self: self, email: emailField.text!, psw: passwordField.text!,Biometric: BiometricAuthentication)
                }
                if (response["registered"] as! Bool) == true{
                    if response["verified"] as! Bool == true{
                        if !BiometricAuthentication{
                            user_id = response["uid"] as! Int
                            
                            guard let email = UserDefaults.standard.object(forKey: "lastAccessedUserName") as? String else {return}
                            LoadPassword(email)
        
                            if (emailField.text! != email || (response["hash"] as! String != passwordKeyChain)) {
                                AlertCredentials(UserEmail: emailField.text!, UserPassword: response["hash"] as! String)
                            }
                        }
                        
                        let vc = segue.destination as! DashboardViewController
                        vc.user_id = response["uid"] as! Int
                    
                    }
                    else{
                        performSegue(withIdentifier: "NotVerified", sender: nil)
                    }
                }
                else{
                    BiometricAuthentication = false
                    self.present(Alert(title: "Error", message: "Credentials are incorrect.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
                
            }
        } else if (segue.identifier == "Move"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        } 
    }
    
    // MARK: - TOUCH/FACE ID    
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
                        if ConnectionTest(self: self) {
                            self.performSegue(withIdentifier: "Dashboard", sender: nil)
                        }
                    }
                }
            })
        }
    }
}


