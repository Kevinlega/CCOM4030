//
//  CreateAccountViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class CreateAccountViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
