//
//  AudioViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 11/12/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit
import AVFoundation


class AudioViewController: UIViewController, AVAudioPlayerDelegate, AVAudioRecorderDelegate {

   
 
    // Variables de los botones y del label
    // mas bien para cambiarlas en el UI.
    @IBOutlet weak var PlayRef: UIButton!
    @IBOutlet weak var RecordRef: UIButton!
    @IBOutlet weak var RecordingTime: UILabel!
    
    // Player y el recorder de audio
    // Timer para desplegar tiempo de grabacion
    // Access: nos dieron acceso?
    // Chequear si esta grabando o reproduciendo
    var Recorder: AVAudioRecorder!
    var Player : AVAudioPlayer!
    var ATimer:Timer!
    var Access: Bool!
    var IsRecording = false
    var IsPlaying = false
    
    var user_id = Int()
    var project_id = Int()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        // Es mejor pedir perdon que pedir permiso. Or is it?
        checkPermission()
    }
    
    // Conseguir permiso del user para iniciar session de grabacion.
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
    
    // Esto es para guardar las grabaciones localmente, lo mas seguro se elimina.
    func getDocumentsDirectory() -> URL{
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = paths[0]
        return documentsDirectory
    }
    
    // Buscando donde se guardara la grabacion.
    func getFileUrl() -> URL{
        let filename = "Recording.m4a"
        let filePath = getDocumentsDirectory().appendingPathComponent(filename)
        return filePath
    }
    
    // Seteando la session de grabacion.
    func setupRecorder(){
        if Access{
            let session = AVAudioSession.sharedInstance()
            do{
                try session.setCategory(.playback, mode: .default)
                try session.setActive(true)
                // Atributos de la grabacion.
                let settings = [ AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
                                 AVSampleRateKey: 44100,
                                 AVNumberOfChannelsKey: 2,
                                 AVEncoderAudioQualityKey:AVAudioQuality.high.rawValue ]
                // Posiblemente el url aqui va a ser el folder en el servidor del proyecto
                // veremos.
                Recorder = try AVAudioRecorder(url: getFileUrl(), settings: settings)
                Recorder.delegate = self
                Recorder.isMeteringEnabled = true
                Recorder.prepareToRecord()
            }
            catch let error{
                displayAlert(msg_title: "Error", msg_desc: error.localizedDescription, action_title: "OK")
            }
        }
        else{
            displayAlert(msg_title: "Error", msg_desc: "Don't have access to use your microphone.", action_title: "OK")
        }
    }
    
    // Desplegar tiempo de la grabacion.
    @objc func updateAudioMeter(timer: Timer){
        if Recorder.isRecording{
            let hr = Int((Recorder.currentTime / 60) / 60)
            let min = Int(Recorder.currentTime / 60)
            let sec = Int(Recorder.currentTime.truncatingRemainder(dividingBy: 60))
            let totalTimeString = String(format: "%02d:%02d:%02d", hr, min, sec)
            RecordingTime.text = totalTimeString
            Recorder.updateMeters()
        }
    }
    
    // Acabamos de grabar.
    func finishedRecording(success: Bool){
        if success{
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
    
    // Seteando para reproducir el sonido
    func preparePlay(){
        do{
            Player = try AVAudioPlayer(contentsOf: getFileUrl())
            Player.delegate = self
            Player.prepareToPlay()
        }
        catch{
            print("Error")
        }
    }
    
    // Cuando presionamos "Play"
    @IBAction func PlayButton(_ sender: Any){
        if(IsPlaying){
            Player.stop()
            RecordRef.isEnabled = true
            PlayRef.setTitle("Play", for: .normal)
            IsPlaying = false
        }
        else{
            // Lo mas seguro aqui vamos a buscar el file al servidor.
            if FileManager.default.fileExists(atPath: getFileUrl().path){
                RecordRef.isEnabled = false
                PlayRef.setTitle("Pause", for: .normal)
                preparePlay()
                Player.play()
                IsPlaying = true
            }
            else{
                displayAlert(msg_title: "Error", msg_desc: "Audio file is missing.", action_title: "OK")
            }
        }
    }
    
    // Cuando presionamos "Record"
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
    
    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool){
        if !flag{
            finishedRecording(success: false)
        }
        PlayRef.isEnabled = true
    }
    
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool){
        RecordRef.isEnabled = true
    }
    
    func displayAlert(msg_title : String , msg_desc : String ,action_title : String){
        let ac = UIAlertController(title: msg_title, message: msg_desc, preferredStyle: .alert)
        ac.addAction(UIAlertAction(title: action_title, style: .default){
            (result : UIAlertAction) -> Void in
            _ = self.navigationController?.popViewController(animated: true)
        })
        present(ac, animated: true)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
}

