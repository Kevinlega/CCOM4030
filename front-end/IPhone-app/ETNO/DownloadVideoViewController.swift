//
//  DownloadVideoViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 12/5/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit
import AVKit
import AVFoundation

import Photos

class DownloadVideoViewController: UIViewController {

    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var location = String()
    var local_url = URL(string: "")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        location = "http://54.81.239.120/projects/1/fb633b48-9850-40ca-ba37-26beb9558892/videos/VIDEO_1_20181204_161530_.mp4"
        
        let url = URL(string: location)!
        
        let task = URLSession.shared.downloadTask(with: url){ localURL, urlResponse, error in
            // Save file from server to tmp file.
            if let localURL = localURL{
                
                    self.local_url = localURL
            
            }
        }
        task.resume()
    }
    
    
    
    @IBAction func saveVideo(_ sender: Any) {
        PHPhotoLibrary.shared().performChanges({
            PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: self.local_url ?? URL(string: self.location)!)
        }) { saved, error in
            if saved {
                let alertController = UIAlertController(title: "Your video was successfully saved", message: nil, preferredStyle: .alert)
                let defaultAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                alertController.addAction(defaultAction)
                self.present(alertController, animated: true, completion: nil)
            }
        }
    }
    
    @IBAction func playVideo(_ sender: Any) {
        let url = URL(string: location)!
        
        // Start transfer
        DispatchQueue.main.async{
            let player = AVPlayer(url: url)
            let playerController = AVPlayerViewController()
            playerController.player = player
            self.present(playerController, animated: true) {
                player.play()
            }
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
