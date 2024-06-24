using System;
using System.Runtime.CompilerServices;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using TheWebApiServer.Data;
using TheWebApiServer.IServices;
using TheWebApiServer.Models;
using TheWebApiServer.Requests;
using TheWebApiServer.Services;

namespace TheWebApiServer.Controllers
{
    [ApiController]
    [Route("[Controller]")]
    public class MusicController : ControllerBase
    {
        
        private readonly DataContext _context;
        private static RecommendationModel _recommendationModel;
        public MusicController(DataContext context, RecommendationModel recommendationModel)
        {
            _context = context;
            _recommendationModel=recommendationModel;
        }

        [HttpGet("GetMusic")]
        [Authorize]
        public async Task<IActionResult> GetMusic([FromQuery] GetMusicRequest model)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("niema takiego uzytwkonika");
            }
            
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var song = await _context.Songs
             .Where(x => x.Id == model.MusicId)
             .Select(x => new
             {
                 x.Id,
                 x.Title,
                 x.MusicLength,
                 x.ArtistId,
                 ArtistName = x.Artist.Name,
                 x.ThumbnailData,
                 x.Views,
                 Likes = _context.Favourites.Count(y => y.SongId == x.Id)
             })
             .FirstOrDefaultAsync();
            if (song == null)
            {
                return NotFound("invalid MusicId");
            }
            return Ok(song);
        }


        [HttpGet("GetProposedMusic")]
        [Authorize]
        public async Task<IActionResult> GetProposedMusic(int CountOfProposedMusic)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("nie ma takiego uzytkownika");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            if (CountOfProposedMusic < 1)
            {
                return BadRequest("incorrect value of CountOfProposedMusic");
            }
            var songsId = _recommendationModel.GetRecommendationsForUser(_context, userId, CountOfProposedMusic);

            var songs = await _context.Songs
                .Where(x => songsId.Contains(x.Id))
                .Select(x => new
                {
                    x.Id,
                    x.Title,
                    x.MusicLength,
                    x.ArtistId,
                    ArtistName = x.Artist.Name,
                    x.ThumbnailData,
                    x.Views,
                    Likes = _context.Favourites.Count(y => y.SongId == x.Id)
                })
                .Take(CountOfProposedMusic)
                .ToListAsync();

            return Ok(songs.ToList());
        }


        [HttpPost("LikeMusic")]
        [Authorize]
        public async Task<IActionResult> LikeMusic([FromQuery] int musicId)
        {
            
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("niema takiego uzytwkonika");
            }
           
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            
            var song = await _context.Songs.FindAsync(musicId);
            if (song == null)
            {
                return BadRequest("Taka muzyka nie istnieje");
            }

            var contains=await _context.Favourites
                .Where(x => x.UserId == userId && x.SongId == musicId)
                .FirstOrDefaultAsync();

            if (contains != null)
                return BadRequest("ta muzyka juz istnieje");


            _context.Favourites.Add(new Favourites
            {
                SongId = musicId,
                UserId = userId,
                AddTime=DateTime.Now
            });

            await _context.SaveChangesAsync();
            return Ok("added to serwer");
        }

        [HttpPost("UnLikeMusic")]
        [Authorize]
        public async Task<IActionResult> UnLikeMusic([FromQuery] int musicId)
        {

            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("nie ma takiego uzytkownika");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var song = await _context.Songs.FindAsync(musicId);
            if (song == null)
            {
                return BadRequest("Taka muzyka nie istnieje");
            }

            var curFavourite = await _context.Favourites
                .SingleOrDefaultAsync(x => x.SongId == musicId && x.UserId == userId);

            if (curFavourite == null)
            {
                return NotFound("Ten użytkownik nie lajkuje tej muzyki");
            }

            _context.Favourites.Remove(curFavourite);
            await _context.SaveChangesAsync();

            return Ok();
        }


        [HttpGet("GetMusicByTitle")]
        [Authorize]
        public async Task<IActionResult> GetMusicByTitle([FromQuery] string titleSubString, int countOfReturnedMusic,string categoryName = null)
        {

            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("nie ma takiego uzytkownika");
            }
            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            var containingTitle = await _context.Songs
                 .Where(x =>
                     x.Title.ToLower().StartsWith(titleSubString.ToLower()) &&
                     (categoryName == null || x.Genre.Name == categoryName)
                 )
                 .Select(x => new
                 {
                     x.Id,
                     x.Title,
                     x.MusicLength,
                     x.ArtistId,
                     ArtistName = x.Artist.Name,
                     x.ThumbnailData,
                     x.Views,
                     Likes = _context.Favourites.Count(y => y.SongId == x.Id)

                 })
                 .Take(countOfReturnedMusic)
                 .ToListAsync();

            var containingTitle2 = await _context.Songs
                .Where(x => x.Title.ToLower().Contains(titleSubString.ToLower()) && !x.Title.ToLower().StartsWith(titleSubString.ToLower()) && (categoryName==null || x.Genre.Name==categoryName))
                .Select(x => new
                {
                    x.Id,
                    x.Title,
                    x.MusicLength,
                    x.ArtistId,
                    ArtistName = x.Artist.Name,
                    x.ThumbnailData,
                    x.Views,
                    Likes = _context.Favourites.Count(y => y.SongId == x.Id)
                })
                .Take(countOfReturnedMusic)
                .ToListAsync();

            var results = containingTitle.Union(containingTitle2).ToList();

            return Ok(results);
        }


        [HttpGet("GetAllGenres")]
        public async Task<IActionResult> GetAllGenres()
        {
            var genres=await _context.Genres
                .Select(x=>x.Name)
                .ToListAsync();


            return Ok(genres);
        }

        [HttpGet("GetLikedUserMusic")]
        [Authorize]
        public async Task<IActionResult> GetLikedUserMusic(int countOfReturnedMusic)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
                return NotFound("nie ma takiego uzytkownika");

            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }
            
            var curUserFavourites = await _context.Favourites
                .Where(x => x.UserId == userId)
                .Select(x => new
                {
                    x.Song.Id,
                    x.Song.Title,
                    x.Song.MusicLength,
                    ArtistName = x.Song.Artist.Name,
                    x.Song.ThumbnailData,
                    x.Song.Views,
                    Likes = _context.Favourites.Count(y => y.SongId == x.SongId)
                })
                .Take(countOfReturnedMusic)
                .ToListAsync();
                
            return Ok(curUserFavourites);
        }

        [HttpGet("IsLiked")]
        [Authorize]
        public async Task<IActionResult> IsLiked([FromQuery] int musicId)
        {
            
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return NotFound("Nie ma takiego użytkownika");
            }

            var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
            if (!userExists)
            {
                return NotFound("Nie ma takiego użytkownika.");
            }

            bool musicExists = await _context.Songs.AnyAsync(x => x.Id == musicId);
            if (!musicExists)
            {
                return NotFound("nie ma takiej muzyki");
            }

            var isFavourite = await _context.Favourites
                .AnyAsync(x => x.UserId == userId && x.SongId == musicId);

            return Ok(isFavourite);
        }


        [HttpGet("GetMusicData")]
       /* [Authorize]*/
        public async Task<IActionResult> GetMusicData(int songId)
        {
            var song = await _context.Songs
            .Where(x => x.Id == songId)
            .Select(x => new { x.MusicData, x.Views })
            .FirstOrDefaultAsync();

            if (song != null)
            {
                var songToUpdate = await _context.Songs.FindAsync(songId);
                if (songToUpdate != null)
                {
                    songToUpdate.Views++;
                    await _context.SaveChangesAsync();
                }


                var stream = new MemoryStream(song.MusicData);
                return new FileStreamResult(stream, "audio/mpeg");
            }

            return NotFound();
        }

    }
}
