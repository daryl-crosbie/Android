using FungiFinder.Model;
using Xamarin.Forms;

namespace FungiFinder.View
{
    public partial class MainPage : ContentPage
    {
        public MainPage()
        {
            InitializeComponent();
        }

        async void ListItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            var mushroom = e.SelectedItem as Mushrooms;
            if (mushroom == null)
                return;
           
            await Navigation.PushAsync(new DetailsPage(mushroom));
            ((ListView)sender).SelectedItem = null;
        }
    }
}
