using Accord.Math;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage;
using Microsoft.ML;
using Microsoft.ML.Trainers;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using TheWebApiServer.Data;
using TheWebApiServer.Models;
using Microsoft.ML.Data;
using Microsoft.ML.Transforms;
using Microsoft.IdentityModel.Tokens;

namespace TheWebApiServer.Services
{
    public class RecommendationModel
    {
        private Dictionary<string, int> _userMapping;
        private MLContext _mlContext;
        private ITransformer _model;
        private IDataView _dataView;
        private bool isInicialise=false;
        public RecommendationModel()
        {
            _mlContext = new MLContext();
            _userMapping = new Dictionary<string, int>();
        }

       
        public void TrainModel(DataContext _context)
        {
            var userIds = _context.Users.Select(x => x.Id).ToList();

            for (int i = 0; i < userIds.Count; i++)
            {
                _userMapping[userIds[i]] = i;
            }

            var ratings = _context.Favourites.Select(x => new MusicRating
            {
                musicId = (uint)x.SongId,
                Label = 1f,
                userId = (uint)_userMapping[x.UserId]
            }).ToList();

            if (ratings.IsNullOrEmpty())
            {
                return;
            }

            _dataView = _mlContext.Data.LoadFromEnumerable(ratings);

            var dataProcessingPipeline = _mlContext.Transforms.Conversion.MapValueToKey(nameof(MusicRating.userId))
                .Append(_mlContext.Transforms.Conversion.MapValueToKey(nameof(MusicRating.musicId)));

            var options = new MatrixFactorizationTrainer.Options
            {
                MatrixColumnIndexColumnName = nameof(MusicRating.userId),
                MatrixRowIndexColumnName = nameof(MusicRating.musicId),
                LabelColumnName = nameof(MusicRating.Label),
                NumberOfIterations = 50,
                ApproximationRank = 50,
                LearningRate = 0.005,
                Lambda = 0.1,
            };

            var trainingPipeline = dataProcessingPipeline.Append(_mlContext.Recommendation().Trainers.MatrixFactorization(options));


            _model = trainingPipeline.Fit(_dataView);
            isInicialise = true;
        }

        public List<int> GetRecommendationsForUser(DataContext _context, string userId, int numberOfRecommendations)
        {
            if (!_userMapping.TryGetValue(userId, out var userIndex))
            {
                throw new ArgumentException("Invalid user ID");
            }


            if (isInicialise)
            {
                var predictionEngine = _mlContext.Model.CreatePredictionEngine<MusicRating, MusicRatingPrediction>(_model);

                var scoredMusic = new List<Tuple<int, float>>();

                var notRatedMusicIds = _context.Songs
                  .OrderByDescending(x => x.Views)
                  .Take(100)
                  .Select(x => x.Id)
                  .Where(songId => !_context.Favourites.Any(f => f.UserId == userId && f.SongId == songId))
                  .ToList();

                foreach (var musicId in notRatedMusicIds)
                {
                    var prediction = predictionEngine.Predict(new MusicRating
                    {
                        userId = (uint)userIndex,
                        musicId = (uint)musicId
                    });

                    scoredMusic.Add(Tuple.Create(musicId, prediction.Score));
                }
                foreach (var a in scoredMusic)
                {
                    Console.WriteLine(a.Item1 + " " + a.Item2);
                }
                return scoredMusic
                   .OrderByDescending(x => Math.Abs(x.Item2))
                   .Take(numberOfRecommendations)
                   .Select(x => x.Item1)
                   .ToList();
            }
            else
            {
                var scoredMusic = _context.Songs
                    .Where(x => !_context.Favourites.Any(f => f.UserId == userId && f.SongId == x.Id))
                    .OrderByDescending(x => x.Views)
                    .Take(numberOfRecommendations)
                    .Select(x => x.Id)
                    .ToList();

                return scoredMusic;
            }
            
        }
    }
    internal class MusicRating
    {
        public uint userId { get; set; }
        public uint musicId { get; set; }
        public float Label { get; set; }
    }
    internal class MusicRatingPrediction
    {
        public float Label { get; set; }
        public float Score { get; set; }
    }
}
