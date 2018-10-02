//
//  ViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 9/27/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    
    @IBOutlet weak var SubmitCredentials: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "CreateAccount"){
            let _ = segue.destination as! CreateAccountViewController
            
        }
    }
    
}

