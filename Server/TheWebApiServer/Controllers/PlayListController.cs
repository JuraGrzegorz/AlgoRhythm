using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using TheWebApiServer.Data;
using TheWebApiServer.IServices;
using TheWebApiServer.Models;
using TheWebApiServer.Services;

namespace TheWebApiServer.Controllers
{
    [ApiController]
    [Route("[Controller]")]
    public class PlayListController: ControllerBase
    {

        private readonly DataContext _context;
        public PlayListController(DataContext context)
        {
            _context = context;
        }

        [HttpPost("CreatePlaylist")]
        [Authorize]
        public async Task<IActionResult> CreatePlaylist([FromQuery]string playlistName,int musicId)
        {

            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if(userId == null)
                return NotFound("nie ma takiego uzytkownika");

            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var song = await _context.Songs.FindAsync(musicId);
            if (song == null)
                return NotFound("nie ma takiej muzyki");


            var curPlayList = new Playlist
            {
                UserId= userId,
                Name= playlistName
            };

            await _context.Playlists.AddAsync(curPlayList);
            await _context.SaveChangesAsync();

            var firstMusincInPlayList = new PlaylistSongs
            {
                SongId= musicId,
                PlayListId=curPlayList.Id
            };
            await _context.PlaylistsSongs.AddAsync(firstMusincInPlayList);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                playListId=firstMusincInPlayList.Id
            });
        }




        [HttpPost("AddSongToPlaylist")]
        [Authorize]
        public async Task<IActionResult> AddSongToPlaylist([FromQuery] int playlistId, int musicId)
        {
           
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if(userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var song = await _context.Songs.FindAsync(musicId);
            if (song == null)
                return NotFound("nie ma takiej muzyki");

            var curPlayList=await _context.Playlists.FindAsync(playlistId);
            if (curPlayList == null)
                return NotFound("nie ma takiej playListy");

            var playlistConstaisMusic=await _context.PlaylistsSongs
                .AnyAsync(x=>x.PlayListId==curPlayList.Id && x.SongId==musicId);

            if (playlistConstaisMusic)
            {
                return BadRequest("ta muzyka juz jest dodana ");
            }

            var newPlayListSong = new PlaylistSongs
            {
                SongId = musicId,
                PlayListId = curPlayList.Id
            };
            await _context.PlaylistsSongs.AddAsync(newPlayListSong);
            await _context.SaveChangesAsync();

            return Ok();
        }

        [HttpPost("DelSongFromPlaylist")]
        [Authorize]
        public async Task<IActionResult> DelSongFromPlaylist([FromQuery] int playlistId, int musicId)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var song = await _context.Songs.FindAsync(musicId);
            if (song == null)
                return NotFound("nie ma takiej muzyki");


            var curPlayList = await _context.Playlists.FindAsync(playlistId);
            if (curPlayList == null)
                return NotFound("nie ma takiej playListy");

            if(curPlayList.UserId != userId)
            {
                return BadRequest("uzytkownik nie ma uprawnien do tej playlisty");
            }

            var curMusic = await _context.PlaylistsSongs
                .Where(x => x.SongId == musicId && playlistId == curPlayList.Id)
                .FirstOrDefaultAsync();
            if(curMusic == null)
            {
                return NotFound("nie ma takiej muzyki w playliscie");
            }
            _context.PlaylistsSongs.Remove(curMusic);
            await _context.SaveChangesAsync();

            return Ok();
        }



        [HttpGet("GetUserPlaylist")]
        [Authorize]
        public async Task<IActionResult> GetUserPlaylist()
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }


            var playlistList = await _context.Playlists
                .Where(x => x.UserId == userId)
                .Select(x => new
                {
                    x.Id,
                    x.Name,
                    countOfMusic = _context.PlaylistsSongs
                        .Where(x => x.PlayListId == x.Id)
                        .Sum(x => x.Id)
                })
                .ToListAsync();


            return Ok(playlistList);
        }


        [HttpGet("GetPlaylistThumbnailData")]
        [Authorize]
        public async Task<IActionResult> GetPlaylistThumbnailData([FromQuery]int playlistId)
        {

            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var curPlaylist=await _context.Playlists
                .Where(x => x.Id == playlistId)
                .FirstOrDefaultAsync();
            //dodac jesli muzyka nie ma zadnych muzyk

            if (curPlaylist == null)
                return NotFound("nie ma takiej playlisty");

            if (curPlaylist.UserId != userId)
            {
                return BadRequest("uzytkownik nie ma uprawnien do tej playlisty");
            }

            var totalDuration = await _context.PlaylistsSongs
               .Where(x => x.PlayListId == playlistId)
               .Select(x=>x.Id)
               .SumAsync();
            if (totalDuration == 0)
                return NotFound("Playlista nie ma muzyki");

            var oldestRecord = await _context.PlaylistsSongs
                .Where(x => x.PlayListId == playlistId)
                .OrderBy(x => x.Id)
                .Take(1)
                .Select(x=>x.Song.ThumbnailData)
                .FirstOrDefaultAsync();

            return Ok(oldestRecord);
        }



        [HttpGet("GetPlaylistMusic")]
        [Authorize]
        public async Task<IActionResult> GetPlaylistMusic([FromQuery] int playlistId, int offset, int countOfReturnedMusic)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }


            var curPlaylist = await _context.Playlists.FindAsync(playlistId);
            if (curPlaylist == null)
                return NotFound("nie ma takiej playListy");

            if (curPlaylist.UserId != userId)
            {
                return BadRequest("uzytkownik nie ma uprawnien do tej playlisty");
            }

            var playlistMusics=await _context.PlaylistsSongs
                .Where(x => x.PlayListId == curPlaylist.Id)
                .Take(countOfReturnedMusic)
                .Select(x => new
                {
                    x.Song.Id,
                    x.Song.Title,
                    x.Song.MusicLength,
                    x.Song.ArtistId,
                    ArtistName=x.Song.Artist.Name,
                    x.Song.ThumbnailData,
                    x.Song.Views,
                    Likes = _context.Favourites.Count(y => y.SongId == x.Id)
                })
                .ToListAsync();

            return Ok(playlistMusics);
        }


        [HttpPost("DelPlaylist")]
        [Authorize]
        public async Task<IActionResult> DelPlaylist([FromQuery]int playlistId)
        {
            
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }


            var curPlaylist = await _context.Playlists.FindAsync(playlistId);
            if (curPlaylist == null)
                return NotFound("nie ma takiej playListy");

            if (curPlaylist.UserId != userId)
            {
                return BadRequest("uzytkownik nie ma uprawnien do tej playlisty");
            }


            _context.Playlists.Remove(curPlaylist);
            await _context.SaveChangesAsync();
            return Ok();
        }

        [HttpGet("SharePlaylist")]
        [Authorize]
        public async Task<IActionResult> SharePlaylist([FromQuery]int playlistId)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }


            var curPlaylist = await _context.Playlists.FindAsync(playlistId);
            if (curPlaylist == null)
                return NotFound("nie ma takiej playListy");

            if (curPlaylist.UserId != userId)
            {
                return BadRequest("uzytkownik nie ma uprawnien do tej playlisty");
            }

            if (curPlaylist.ShareCode != null)
            {
                return Ok(new { ShareCode = curPlaylist.ShareCode });
            }

            string shareCode;
            do
            {
                shareCode = GenerateShareCode();
            }
            while (await _context.Playlists.AnyAsync(p => p.ShareCode == shareCode));

            curPlaylist.ShareCode = shareCode;

            await _context.SaveChangesAsync();

            return Ok(new { ShareCode = curPlaylist.ShareCode });
        }

        [HttpGet("GetSharedPlaylist")]
        [Authorize]
        public async Task<IActionResult> GetSharedPlaylist([FromQuery] string shareCode)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
           
            var curPlaylist = await _context.Playlists
                .Where(x => x.ShareCode == shareCode)
                .FirstOrDefaultAsync();
           
            if (curPlaylist == null)
                return NotFound("nie ma takiej playListy");


            Playlist newPlaylist = new Playlist
            {
                Name = curPlaylist.Name,
                UserId = userId,
                ShareCode = null
            };


            await _context.Playlists.AddAsync(newPlaylist);
            await _context.SaveChangesAsync();

            var playlistSongs=await _context.PlaylistsSongs
                .Where(x=>x.PlayListId==curPlaylist.Id)
                .Select(x=>x.SongId)
                .ToListAsync();

            foreach(var song in playlistSongs)
            {
                PlaylistSongs curplaylist = new PlaylistSongs
                {
                    SongId=song,
                    PlayListId=newPlaylist.Id
                };

                await _context.PlaylistsSongs.AddAsync(curplaylist);
            }
            await _context.SaveChangesAsync();
            return Ok();
        }






        private string GenerateShareCode()
        {
            
            var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            var random = new Random();
            var token = new char[8];

            for (int i = 0; i < token.Length; i++)
            {
                token[i] = chars[random.Next(chars.Length)];
            }

            return new string(token);
        }




    }
}
