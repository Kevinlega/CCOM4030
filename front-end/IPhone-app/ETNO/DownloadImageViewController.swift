// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : DownloadImageViewController.swift
// Description : View controller that lets the user download Image file
//               and displays it.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit

class DownloadImageViewController: UIViewController {

    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var location = String()
    var saved = false
    
    @IBOutlet weak var Save: UIButton!
    @IBOutlet weak var imageView: UIImageView!
    
    
    // Downloads file
    override func viewDidLoad() {
        super.viewDidLoad()
        
        Save.isHidden = true
        let url = URL(string: location)!

        // Start download
        let task = URLSession.shared.downloadTask(with: url){ localURL, urlResponse, error in
            // Save file from server to tmp file.
            if let localURL = localURL{
                
                // gets the data from download file
                if let img =  try? Data(contentsOf: localURL){
                    
                    // Cast data to UIImage
                    let viewIMG = UIImage(data: img)
                    // Place image in UIImageView
                    // Note: for some reason this has to be done in the main thread
                    DispatchQueue.main.async{
                        self.imageView.image = viewIMG
                        self.Save.isHidden = false
                    }
                }
            }
        }
        task.resume()
        // Do any additional setup after loading the view.
    }
    
    
    
    // Save image to gallery
    @IBAction func savePhoto(_ sender: Any) {
        if !saved{
            guard let selectedImage = imageView.image else {
                print("Image not found!")
                return
            }
            UIImageWriteToSavedPhotosAlbum(selectedImage, self, nil, nil)
            self.present(Alert(title: "Saved", message: "Image saved in gallery.", Dismiss: "Dismiss"),animated: true, completion: nil)
        } else {
            self.present(Alert(title: "Nothing to Save", message: "Already saved the image.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
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
