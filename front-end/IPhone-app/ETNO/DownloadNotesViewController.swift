// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : DownloadNotesViewController.swift
// Description : View controller that lets the user download text file
//               and displays it.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit

class DownloadNotesViewController: UIViewController {

    @IBOutlet weak var Label: UITextView!
    var user_id = Int()
    var project_id = Int()
    var location = String()
    
    override func viewDidLoad() {
    
        // Constructed path to file
        let url = URL(string: location)!

        // Start download
        let task = URLSession.shared.downloadTask(with: url){ localURL, urlResponse, error in
            // Save file from server to tmp file.
            if let localURL = localURL{
        
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
    
    // Handles the data
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        ConnectionTest(self: self)
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
}
