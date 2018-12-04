//
//  ProjectViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/4/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit

class ProjectViewController: UIViewController, UINavigationControllerDelegate{
    
    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var is_admin = Bool()
    var project_path = String()
    
    @IBOutlet weak var AddParticipant: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        is_admin = CheckAdmin(project_id: project_id, user_id: user_id)
        if !is_admin{
            AddParticipant.isHidden = true
        }
        
        // Create Request
        
        let url = URL(string: "http://54.81.239.120/selectAPI.php");
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=10&pid=\(project_id)";
        request.httpBody = post.data(using: String.Encoding.utf8);
        
        let response = ConnectToAPI(request: request)
        
        if (response["empty"] as! Bool) == false{
            project_path = response["path"] as! String
        }
        print(project_path)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "AddParticipants"){
            let vc = segue.destination as! AddParticipantViewController
            vc.user_id = user_id
            vc.project_id = project_id
        } else if (segue.identifier == "BackToDashboard"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        } else if (segue.identifier == "AudioSegue"){
            let vc = segue.destination as! AudioViewController
            vc.user_id = user_id
            vc.project_id = project_id
        } else if (segue.identifier == "CameraSegue"){
            let vc = segue.destination as! CameraViewController
            vc.user_id = user_id
            vc.project_id = project_id
        } else if (segue.identifier == "NotesSegue"){
            let vc = segue.destination as! NotesViewController
            vc.user_id = user_id
            vc.project_id = project_id
        } else if (segue.identifier == "VideoSegue"){
            let vc = segue.destination as! VideoViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
        
    }
}
