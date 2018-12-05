//
//  DownloadImageViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 12/5/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class DownloadImageViewController: UIViewController {

    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var location = String()
    var saved = false
    
    @IBOutlet weak var Save: UIButton!
    @IBOutlet weak var imageView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        Save.isHidden = true
//        let url = URL(string: location)!

        let url = URL(string: "http://54.81.239.120/projects/1/fb633b48-9850-40ca-ba37-26beb9558892/images/IMAGE_1_20181204_160818_.jpg")!
        // Start transfer
        let task = URLSession.shared.downloadTask(with: url){ localURL, urlResponse, error in
            // Save file from server to tmp file.
            if let localURL = localURL{
                // Switch case for each supported file type
                
                // Received a text file, just cast to string
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
    
    

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }

}
