// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : AudioViewController.swift
// Description : View controller that records audio using AVFoundation library
// Copyright © 2018 Los Duendes Malvados. All rights reserved.


import UIKit
import AVFoundation


class AudioViewController: UIViewController, AVAudioPlayerDelegate, AVAudioRecorderDelegate {

   
    // Buttons and labels that will be changed in the view dynamically.
    // For example, if user presses record button, change record button to "Stop Recording" button, etc.
    @IBOutlet weak var PlayRef: UIButton!
    @IBOutlet weak var RecordRef: UIButton!
    @IBOutlet weak var RecordingTime: UILabel!
    
    // Audio recorder and player
    // Timer to display audio/recording duration
    // Ask user for access to device microphone
    // Check if user is recording or playing for UI.
    var Recorder: AVAudioRecorder!
    var Player : AVAudioPlayer!
    var ATimer:Timer!
    var Access: Bool!
    var IsRecording = false
    var IsPlaying = false
    
    // Identify user and project
    var user_id = Int()
    var project_id = Int()
    var projectPath = String()
    
    // When view loads
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        // Ask user for access to microphone
        checkPermission()
        
    }
    
    // Get permision from user to initiate audio session.
    func checkPermission(){
        switch AVAudioSession.sharedInstance().recordPermission{
        case AVAudioSession.RecordPermission.granted:
            Access = true
            break
        case AVAudioSession.RecordPermission.denied:
            Access = false
            break
        case AVAudioSession.RecordPermission.undetermined:
            AVAudioSession.sharedInstance().requestRecordPermission({(allowed) in
                if allowed{
                    self.Access = true
                }
                else{
                    self.Access = false
                }
            })
            break
        default:
            break
        }
    }
    
    // Get a directory to save file locally
    func getDocumentsDirectory() -> URL{
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = paths[0]
        return documentsDirectory
    }
    
    // Get file location
    func getFileUrl() -> URL{
        let filename = "Recording.m4a"
        let filePath = getDocumentsDirectory().appendingPathComponent(filename)
        return filePath
    }
    
    // Setting up recording session instance.
    // Depends on user access.
    func setupRecorder(){
        if Access{
            let session = AVAudioSession.sharedInstance()
            do{
                // Setting up session
                try session.setCategory(.playAndRecord, mode: .default)
                try session.setActive(true)
                // Recording attributes
                let settings = [ AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
                                 AVSampleRateKey: 44100,
                                 AVNumberOfChannelsKey: 2,
                                 AVEncoderAudioQualityKey:AVAudioQuality.high.rawValue ]
                // Initiate recorder
                Recorder = try AVAudioRecorder(url: getFileUrl(), settings: settings)
                Recorder.delegate = self
                Recorder.isMeteringEnabled = true
                Recorder.prepareToRecord()
            }
            // Error handling
            catch let error{
                displayAlert(msg_title: "Error", msg_desc: error.localizedDescription, action_title: "OK")
            }
        }
        else{
            displayAlert(msg_title: "Error", msg_desc: "Don't have access to use your microphone.", action_title: "OK")
        }
    }
    
    // Display duration of recording/audio
    @objc func updateAudioMeter(timer: Timer){
        if Recorder.isRecording{
            // Formating time...
            let hr = Int((Recorder.currentTime / 60) / 60)
            let min = Int(Recorder.currentTime / 60)
            let sec = Int(Recorder.currentTime.truncatingRemainder(dividingBy: 60))
            let totalTimeString = String(format: "%02d:%02d:%02d", hr, min, sec)
            
            // Change label
            RecordingTime.text = totalTimeString
            Recorder.updateMeters()
        }
    }
    // Finished recording, stop recording session
    func finishedRecording(success: Bool){
        if success{
            
            print(Recorder.currentTime)
            
            Recorder.stop()
            
            Recorder = nil
            ATimer.invalidate()
            
            print("recorded successfully.")
        }
        else
        {
            displayAlert(msg_title: "Error", msg_desc: "Recording failed.", action_title: "OK")
        }
    }
    
    // Setup audio player session instance.
    func preparePlay(){
        do{
            Player = try AVAudioPlayer(contentsOf: getFileUrl())
            Player.delegate = self
            Player.prepareToPlay()
        }
        // ERROR HANDLING
        catch{
            print("Error")
        }
    }
    
    // When "Play" button is pressed, play recording and change button labels.
    // If currently playing, stops playing session.
    @IBAction func PlayButton(_ sender: Any){
        if(IsPlaying){
            Player.stop()
            RecordRef.isEnabled = true
            PlayRef.setTitle("Play", for: .normal)
            IsPlaying = false
        }
        else{
            // Does file exist?
            if FileManager.default.fileExists(atPath: getFileUrl().path){
                RecordRef.isEnabled = false
                PlayRef.setTitle("Stop", for: .normal)
                preparePlay()
                Player.play()
                IsPlaying = true
            }
            // Error handling
            else{
                displayAlert(msg_title: "Error", msg_desc: "Audio file is missing.", action_title: "OK")
            }
        }
    }
    
    // When "Record" button is pressed, start recording and change labels.
    // If currently recording, stop recording session.
    @IBAction func RecordButton(_ sender: Any){
        if(IsRecording){
            finishedRecording(success: true)
            RecordRef.setTitle("Record", for: .normal)
            PlayRef.isEnabled = true
            IsRecording = false
        }
        else{
            setupRecorder()
            Recorder.record()
            ATimer = Timer.scheduledTimer(timeInterval: 0.1, target:self, selector:#selector(self.updateAudioMeter(timer:)), userInfo:nil, repeats:true)
            RecordRef.setTitle("Stop", for: .normal)
            PlayRef.isEnabled = false
            IsRecording = true
        }
    }
    // If "Record" button is pressed for the second time invoke function to stop recording session.
    // Allow user to play recording.
    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool){
        if !flag{
            finishedRecording(success: false)
        }
        PlayRef.isEnabled = true
    }
    
    // Allow user to record another audio file.
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool){
        RecordRef.isEnabled = true
    }
    
    // Pop-up alert for user, mostly used for error handling
    func displayAlert(msg_title : String , msg_desc : String ,action_title : String){
        let ac = UIAlertController(title: msg_title, message: msg_desc, preferredStyle: .alert)
        ac.addAction(UIAlertAction(title: action_title, style: .default){
            (result : UIAlertAction) -> Void in
            _ = self.navigationController?.popViewController(animated: true)
        })
        present(ac, animated: true)
    }
    
    // Segue back to project, pass user and project info
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        ConnectionTest(self: self)
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
    
    
    
    
    // --- Uploading an audio file to server
    // "Upload" button is pressed, initiate request by invoking function.
    @IBAction func Upload(_ sender: Any){
        myVoiceUploadRequest()
    }

    // Send file to API to be stored in project folder.
    func myVoiceUploadRequest(){
     
        // URL of API
        let myUrl = NSURL(string: "http://54.81.239.120/fUploadAPI.php");
        
        // Audio file will be converted to binary data and sent through request body.
        let request = NSMutableURLRequest(url:myUrl! as URL);
        request.httpMethod = "POST";
        
        // POST parameters for API
        // fileType is given, and path to save file depends on user and project
        let param = ["fileType":"1","path":(projectPath + "/voice/")]
        
        // Hash to identify request
        let boundary = generateBoundaryString()
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        // Convert audio recording to Data
        let voiceData = try? Data(contentsOf: getFileUrl())
        // If no data, do nothing
        if(voiceData==nil)  { return; }
        
        // Create request body
        request.httpBody = createBodyWithParameters(parameters: param, filePathKey: "file", imageDataKey: voiceData! as NSData, boundary: boundary) as Data
        
        // Initiate request to webserver.
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            // Error handling
            if error != nil {
                print("error=\(String(describing: error))")
                return
            }
            
            // Print out response object
            print("******* response = \(String(describing: response))")
            
            // Print out reponse body
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("****** response data = \(responseString!)")
            
            do {
                // Handle response from server
                let json = try JSONSerialization.jsonObject(with: data!, options: []) as? NSDictionary
                if json!["file_created"] as! Bool == true{
                    self.present(Alert(title: "Uploaded", message: "You may see it from project view.", Dismiss: "Dismiss"),animated: true, completion: nil)
                } else{
                    self.present(Alert(title: "Try Again", message: "Error uploading.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
                
            }
            // Error handling
            catch {
                print(error)
            }
        }
        task.resume()
    }
    
    // Create request body by inserting POST parameters
    func createBodyWithParameters(parameters: [String: String]?, filePathKey: String?, imageDataKey: NSData, boundary: String) -> NSData {
        let body = NSMutableData();
        
        // insert parameters into body
        if parameters != nil {
            for (key, value) in parameters! {
                body.appendString(string: "--\(boundary)\r\n")
                body.appendString(string: "Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n")
                body.appendString(string: "\(value)\r\n")
            }
        }
        // Format date to name file
        let dateFormatter : DateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMdd_HHmmss"
        let date = Date()
        let dateString = dateFormatter.string(from: date)
        let filename = "VOICE_\(user_id)_" + dateString + "_.m4a"
        
        // File-type
        let mimetype = "voice/m4a"
        
        // File appending to POST
        body.appendString(string: "--\(boundary)\r\n")
        body.appendString(string: "Content-Disposition: form-data; name=\"\(filePathKey!)\"; filename=\"\(filename)\"\r\n")
        body.appendString(string: "Content-Type: \(mimetype)\r\n\r\n")
        body.append(imageDataKey as Data)
        body.appendString(string: "\r\n")
        body.appendString(string: "--\(boundary)--\r\n")
        return body
    }
    
    // Generate a hash for boundary
    func generateBoundaryString() -> String {
        return "Boundary-\(NSUUID().uuidString)"
    }
}
