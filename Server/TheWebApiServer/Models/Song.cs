using System.ComponentModel.DataAnnotations.Schema;

namespace TheWebApiServer.Models
{
    public class Song
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public DateTime ReleaseDate { get; set; }
        public int ArtistId {  get; set; }
        [ForeignKey("ArtistId")]
        public Artist Artist { get; set; }
        public byte[] MusicData{ get; set; }
        public List<PlaylistSongs> PlaylistSongs { get; set; }
        public int GenreId {  get; set; }
        [ForeignKey("GenreId")]
        public Genre Genre { get; set; }
        
    }
}
