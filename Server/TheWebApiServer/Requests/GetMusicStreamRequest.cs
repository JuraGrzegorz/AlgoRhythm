using System.ComponentModel.DataAnnotations;

namespace TheWebApiServer.Requests
{
    public class GetMusicStreamRequest
    {
        [Required]
        public int MusicId { get; set; }
        [Required]
        public string SocketId { get; set; }
        [Required]
        public int MusicOffSet { get; set; }
        [Required]
        public int SizeOfMusicData {  get; set; }
        [Required]
        public int SizeOfDataFrame { get; set; }
    }
}
