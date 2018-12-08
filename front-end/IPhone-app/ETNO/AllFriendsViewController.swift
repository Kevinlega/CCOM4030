// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : AppDelegate.swift
// Description : View controller that displays friendlist of a user
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit

class AllFriendsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate{

    // MARK: - Variables
    // Flag that indicates if the admin has users to be added
    var UsersCanBeAdded = false
    
    // Variables passed from previous view
    var user_id = Int()
    
    // List of every user in the data base that is not in the project
    var users = [String()]
    var usersEmail = [String()]
    // Will Filtered the Users from using the Search Bar
    var FilteredUsers = [String()]
    // Users selected by the admin to add to the project
    
    // Flags to know if the list was empty and if the admin is searching for users.
    var FirstSelected = true
    var Searching = false
    
    // Connections to the app view
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    // MARK: - Modify the Tableview
    // Update the view of the table
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if Searching{
            return FilteredUsers.count
        } else{
            if(users[0] == ""){
                return 0
            } else{
                return users.count
            }
        }
    }
    
    // Display users in table view
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
        if Searching{
            cell.textLabel?.text = FilteredUsers[indexPath.row]
        }
        else{
            cell.textLabel?.text = users[indexPath.row]
        }
        
        return cell
    }
    
    // MARK: - Search Bar Actions
    // Use the search bar to search users
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchBar.text == nil || searchBar.text == ""{
            Searching = false
            view.endEditing(true)
            FilteredUsers = []

        }
        else {
            Searching = true
            FilteredUsers = users.filter({$0.localizedCaseInsensitiveContains(searchBar.text!)})
        }
        
        tableView.reloadData()
    }
    
    // MARK: - Default Functions
    // When view loads, receive user id, and display friends
    override func viewDidLoad() {
        super.viewDidLoad()
        user_id = TabBarViewController.User.uid
        hideKeyboardWhenTappedAround()

        let response = GetFriends(user_id: user_id)
        if (response["empty"] as! Bool) == false{
            users = response["name"] as! [String]
            usersEmail = response["email"] as! [String]
        }
        // Do any additional setup after loading the view.
    }
    
    // Default
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Segue Function
    // Prepare segue for user validation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        ConnectionTest(self: self)
        
        if (segue.identifier == "BackToDashboard"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        }
    }
}
