//
//  ProjectViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/4/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit

class ProjectViewController: UIViewController, UINavigationControllerDelegate, UITableViewDelegate, UITableViewDataSource{
  
    
  
    
    
    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var is_admin = Bool()
    var project_path = String()
    var noPhotos = false
    var FileName : NSArray = []
    var location = String()
    
    @IBOutlet weak var AddParticipant: UIButton!
    @IBOutlet weak var table: UITableView!
    

    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if noPhotos{
            return 0
        }
        return FileName.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = UITableViewCell(style: UITableViewCell.CellStyle.default, reuseIdentifier: "cell") as UITableViewCell
        
        let item = FileName[indexPath.row] as! [String:Any]
        cell.textLabel?.text = (item["filename"] as! String)
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //Access the array that you have used to fill the tableViewCell
        let item = FileName[indexPath.row] as! [String:Any]
        
        let name = item["filename"] as! String
        let type = item["type"] as! String
 
        var path = ""
        if let range = project_path.range(of: "p") {
            path = String(project_path[range.lowerBound...])
        }
        
        
        location = "http://54.81.239.120/" + path + "/" + type + "/" + name
        
        switch type {
        case "images":
            performSegue(withIdentifier: "DownloadImage", sender: nil)
            break
        
        case "docs":
            performSegue(withIdentifier: "DownloadNotes", sender: nil)
            break
        case "voice":
            performSegue(withIdentifier: "DownloadAudio", sender: nil)
            break
        case "videos":
            performSegue(withIdentifier: "DownloadVideo", sender: nil)
            break
        default:
            break
        }
    }
    
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
        
        fetchPhotos()
        
        // Do any additional setup after loading the view.
    }

    func fetchPhotos()
    {
        let url_parse = URL(string: "http://54.81.239.120/listdir.php?path=\(project_path)")
        if url_parse != nil {
            let task = URLSession.shared.dataTask(with: url_parse! as URL, completionHandler: {(data, response, error) -> Void in
                do
                {
                    let jsonRes = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String: AnyObject]
                    
                    if jsonRes["empty"] as! Bool == false{
                        self.FileName = jsonRes["files"] as! NSArray
                    }
                    
                    DispatchQueue.main.async {
                        self.table.reloadData()
                    }
                }catch{}
                
            })
            task.resume()
        }
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
            vc.projectPath = project_path
        } else if (segue.identifier == "CameraSegue"){
            let vc = segue.destination as! CameraViewController
            vc.user_id = user_id
            vc.project_id = project_id
            vc.projectPath = project_path

        } else if (segue.identifier == "NotesSegue"){
            let vc = segue.destination as! NotesViewController
            vc.user_id = user_id
            vc.project_id = project_id
            vc.projectPath = project_path

        } else if (segue.identifier == "VideoSegue"){
            let vc = segue.destination as! VideoViewController
            vc.user_id = user_id
            vc.project_id = project_id
            vc.projectPath = project_path

        } else if (segue.identifier == "DownloadNotes"){
            let vc = segue.destination as! DownloadNotesViewController
            vc.user_id = user_id
            vc.project_id = project_id
            vc.location = location

        } else if (segue.identifier == "DownloadImage"){
            let vc = segue.destination as! DownloadImageViewController
            vc.user_id = user_id
            vc.project_id = project_id
            vc.location = location

        } else if (segue.identifier == "DownloadVideo"){
            let vc = segue.destination as! DownloadVideoViewController
            vc.user_id = user_id
            vc.project_id = project_id
            vc.location = location

        } else if (segue.identifier == "DownloadAudio"){
            let vc = segue.destination as! DownloadAudioViewController
            vc.user_id = user_id
            vc.project_id = project_id
            vc.location = location
        }
    }
}
