using System.ComponentModel.DataAnnotations.Schema;

namespace TheWebApiServer.Models
{
    public class SongsTags
    {
        public int Id { get; set; }
        public int SongId { get; set; }
        [ForeignKey("SongId")]
        public Song Song { get; set; }
        public int TagId { get; set; }
        [ForeignKey("TagId")]
        public Tag Tag { get; set; }
    }
}
