//
//  CreateAProjectViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit

class CreateAProjectViewController: UIViewController {
    
    // MARK: - Variables
    
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
            
            let alertController = UIAlertController(title: "Error", message: "All fields are requiered.", preferredStyle: UIAlertController.Style.alert)
            alertController.addAction(UIAlertAction.init(title: "Dismiss", style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
            
            self.present(alertController, animated: true, completion: nil)
        }
        else{
            CanProjectBeAdded = true
        }
    }
    
    // MARK: - Creates the project
    
    func CreateProject(){
        let name = projectName.text!
        let description = projectDescription.text!
        let location = projectLocation.text!
        let folder_link = "the_link"

        // Create the request to the API
        let QueryType = "2"
        let url = URL(string: "http://54.81.239.120/createProjectAPI.php")
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&name=\(name)&description=\(description)&location=\(location)&folder_link=\(folder_link)&user_id=\(user_id)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        
        let group = DispatchGroup()
        group.enter()
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            
            if (error != nil) {
                print("error=\(error!)")
                return
            }
            // print("response = \(response!)")
            do {
                let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                
                if let parseJSON = json {
                    let queryResponse = (parseJSON["created"] as? Bool)!
                    if (queryResponse == true){
                        print("Project created successfully.")
                    }
                    else{
                        print("Uh Oh")
                    }
                }
            }
            catch {
                print(error)
            }
            group.leave()
        }
        task.resume()
        group.wait()
        return
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
        if (segue.identifier == "BackToDashboard"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        }
        else if (segue.identifier == "CreateProject"){
            if CanProjectBeAdded{
                CreateProject()
                let vc = segue.destination as! DashboardViewController
                vc.user_id = user_id
                
            }
        }
    }
}
