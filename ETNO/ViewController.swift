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
    var user_id = 0
    
    @IBOutlet weak var SubmitCredentials: UIButton!
    
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
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
            
        }
    }
}

