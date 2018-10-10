//
//  CreateAProjectViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los 5. All rights reserved.
//
import UIKit

class CreateAProjectViewController: UIViewController {
    
    var user_id = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToDashboard"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        }
        else if (segue.identifier == "CreateProject"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
            
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
