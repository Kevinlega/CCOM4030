// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : DownloadVideoViewController.swift
// Description : View controller that lets the user download video file
//               and play it.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit
import MobileCoreServices

import AVKit
import AVFoundation
import Photos

class DownloadVideoViewController: UIViewController {

    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var location = String()
    var saved = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    // saves the video to the local library
    @IBAction func saveVideo(_ sender: Any) {
        if !saved{
            DispatchQueue.global(qos: .background).async {
                if let url = URL(string: self.location),
                    let urlData = NSData(contentsOf: url) {
                    let documentsPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0];
                    let filePath="\(documentsPath)/tempFile.mp4"
                    DispatchQueue.main.async {
                        urlData.write(toFile: filePath, atomically: true)
                        PHPhotoLibrary.shared().performChanges({
                            PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: URL(fileURLWithPath: filePath))
                        }) { completed, error in
                            if completed {
                                print("Video is saved!")
                            }
                        }
                    }
                }
            }
            saved = true
            self.present(Alert(title: "Saved", message: "You may see the video in gallery.", Dismiss: "Dismiss"),animated: true, completion: nil)
        } else{
            self.present(Alert(title: "Already Saved", message: "No need to save again.", Dismiss: "Dismiss"),animated: true, completion: nil)

        }
    }
   
    // Streams the video from url
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
