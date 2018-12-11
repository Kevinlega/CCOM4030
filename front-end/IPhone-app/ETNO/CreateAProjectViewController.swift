// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : CreateAProjectViewController.swift
// Description : View controller that lets the user create projects
//               and be the administrator.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.


import UIKit

class CreateAProjectViewController: UIViewController {
    
    // MARK: - Variables
    var project_id = Int()
    var user_id = Int()
    var CanProjectBeAdded = false
    
    @IBOutlet weak var projectName: UITextField!
    @IBOutlet weak var projectDescription: UITextField!
    @IBOutlet weak var projectLocation: UITextField!
    
    
    // MARK: - Verifies that Project Can be Created
    @IBAction func CanProjectBeCreated(_ sender: Any) {
        let ProjectName = projectName.text
        let ProjectDescription = projectDescription.text
        let ProjectLocation = projectLocation.text
        
        if (ProjectDescription!.isEmpty || ProjectLocation!.isEmpty || ProjectName!.isEmpty ){
            
            self.present(Alert(title: "Error", message: "All fields are requiered.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
        else{
            CanProjectBeAdded = true
        }
    }
    

    
    // MARK: - Default Functions
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
    // Handles the data
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        ConnectionTest(self: self)
        if (segue.identifier == "BackToDashboard"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        }
        else if (segue.identifier == "CreateProject"){
            if CanProjectBeAdded{
                let response = CreateProject(user_id: user_id,name: projectName.text!, description: projectDescription.text!, location: projectLocation.text!)
                
                if (response["created"] as! Bool) ==  true{
                    let vc = segue.destination as! ProjectViewController
                    vc.user_id = user_id
                    vc.project_id = response["project_id"] as! Int
                }
            }
        }
    }
}
