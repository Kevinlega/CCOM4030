//
//  DownloadNotesViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 12/5/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class DownloadNotesViewController: UIViewController {

    @IBOutlet weak var Label: UITextView!
    var user_id = Int()
    var project_id = Int()
    var location = String()
    
    override func viewDidLoad() {
    
        // Contructed path to file
        let url = URL(string: location)!

//        let url = URL(string: "http://54.81.239.120/projects/1/fb633b48-9850-40ca-ba37-26beb9558892/docs/test.txt")!
        // Start transfer
        let task = URLSession.shared.downloadTask(with: url){ localURL, urlResponse, error in
            // Save file from server to tmp file.
            if let localURL = localURL{
                // Switch case for each supported file type
        
                // Received a text file, just cast to string
           
                if let string = try? String(contentsOf: localURL){
                    // Pass string to textView
                    DispatchQueue.main.async{
                        self.Label.text = string
                    }
                }
            }
        }
        task.resume()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
}
