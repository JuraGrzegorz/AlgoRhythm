using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using TheWebApiServer.Models;

namespace TheWebApiServer.Data
{
    public class DataContext : IdentityDbContext
    {
        public DataContext(DbContextOptions options) : base(options)
        {
        }

        public DbSet<Artist> Artists { get; set; }
        public DbSet<Playlist> Playlists { get; set; }  
        public DbSet<PlaylistSongs> PlaylistsSongs { get; set;}
        public DbSet<Song> Songs { get;set; }
        public DbSet<Favourites> Favourites { get; set; }
        public DbSet<Tag> Tags { get; set; }
        public DbSet<Genre> Genres { get; set; }
        public DbSet<TimePerTag> TimePerTags { get; set; }
        public DbSet<SongsTags>SongsTags { get; set; }
    }
}
